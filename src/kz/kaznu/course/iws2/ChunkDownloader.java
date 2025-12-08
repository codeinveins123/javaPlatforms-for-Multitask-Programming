package kz.kaznu.course.iws2;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.*;
import java.io.*;
import java.nio.file.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

public class ChunkDownloader
{
    private static final Logger logger = Logger.getLogger(ChunkDownloader.class.getName());
    
    private HttpURLConnection conn;
    private long fileSize;
    private ExecutorService executor;
    private CountDownLatch latch;
    private String outputFolder;
    private String fileName;
    private ProgressTracker progressTracker;
    private int numberOfChunks;
    private AtomicLong totalDownloaded;
    private boolean enableLogging = false;
    
    public ChunkDownloader(HttpURLConnection conn, String outputFolder, String fileName, 
                          int numberOfChunks, ProgressTracker tracker)
    {
        this.conn = conn;
        this.fileSize = conn.getContentLengthLong();
        this.outputFolder = outputFolder;
        this.fileName = fileName;
        this.numberOfChunks = numberOfChunks;
        this.progressTracker = tracker;
        this.totalDownloaded = new AtomicLong(0);
        
        if (progressTracker != null)
        {
            progressTracker.setFileSize(fileSize);
        }
    }
    
    public void setEnableLogging(boolean enable)
    {
        this.enableLogging = enable;
    }
    
    public boolean download()
    {
        if (fileSize <= 0)
        {
            String errorMsg = "Не удалось определить размер файла";
            System.out.println(errorMsg);
            if (enableLogging)
            {
                logger.warning(errorMsg);
            }
            return false;
        }
        
        if (enableLogging)
        {
            logger.info("Начинаем многопоточную загрузку файла " + fileName + 
                       " с использованием " + numberOfChunks + " потоков");
        }
        
        executor = Executors.newFixedThreadPool(numberOfChunks);
        latch = new CountDownLatch(numberOfChunks);
        
        long chunkSize = fileSize / numberOfChunks;
        for (int i = 0; i < numberOfChunks; i++)
        {
            int chunkNumber = i;
            long startByte = i * chunkSize;
            long endByte = 0;
            
            if (i == numberOfChunks - 1)
            {
                endByte = fileSize - 1;
            }
            else
            {
                endByte = (i + 1) * chunkSize - 1;
            }
            
            final long realEndByte = endByte;
            
            if (enableLogging)
            {
                logger.fine("Запуск потока для чанка " + chunkNumber + 
                          " (байты " + startByte + "-" + realEndByte + ")");
            }
            
            executor.submit(() -> downloadChunk(chunkNumber, startByte, realEndByte));
        }
        
        try
        {
            latch.await();
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
            
            if (enableLogging)
            {
                logger.info("Все потоки завершили загрузку чанков для файла " + fileName);
            }
            
            return mergeChunks();
        }
        catch (InterruptedException e)
        {
            String errorMsg = "Загрузка прервана: " + e.getMessage();
            System.out.println(errorMsg);
            if (enableLogging)
            {
                logger.warning(errorMsg);
            }
            executor.shutdownNow();
            return false;
        }
        finally
        {
            cleanupTempFiles();
        }
    }
    
    private void downloadChunk(int chunkNumber, long startByte, long endByte)
    {
        HttpURLConnection chunkConn = null;
        try
        {
            URL url = conn.getURL();
            chunkConn = (HttpURLConnection) url.openConnection();
            chunkConn.setRequestMethod("GET");
            chunkConn.setConnectTimeout(15000);
            chunkConn.setReadTimeout(30000);
            chunkConn.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
            
            int responseCode = chunkConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_PARTIAL && responseCode != HttpURLConnection.HTTP_OK)
            {
                String errorMsg = "Чанк " + chunkNumber + " провален: HTTP " + responseCode;
                System.out.println(errorMsg);
                if (enableLogging)
                {
                    logger.warning(errorMsg);
                }
                return;
            }
            
            String chunkFile = outputFolder + File.separator + fileName + ".chunk_" + chunkNumber;
            
            if (enableLogging)
            {
                logger.fine("Чанк " + chunkNumber + " сохраняется в: " + chunkFile);
            }
            
            try (InputStream in = chunkConn.getInputStream();
                 FileOutputStream out = new FileOutputStream(chunkFile))
            {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long chunkDownloaded = 0;
                
                while ((bytesRead = in.read(buffer)) != -1)
                {
                    out.write(buffer, 0, bytesRead);
                    chunkDownloaded += bytesRead;
                    
                    long total = totalDownloaded.addAndGet(bytesRead);
                    if (progressTracker != null)
                    {
                        progressTracker.updateProgress(chunkNumber, chunkDownloaded, total);
                    }
                }
                
                String successMsg = "Чанк " + chunkNumber + " загружен: " + chunkDownloaded + " байт";
                System.out.println(successMsg);
                if (enableLogging)
                {
                    logger.fine(successMsg);
                }
            }
        }
        catch (Exception e)
        {
            String errorMsg = "Не смог скачать чанк " + chunkNumber + ": " + e.getMessage();
            System.out.println(errorMsg);
            if (enableLogging)
            {
                logger.warning(errorMsg);
            }
        }
        finally
        {
            if (chunkConn != null)
            {
                chunkConn.disconnect();
            }
            latch.countDown();
        }
    }
    
    private boolean mergeChunks()
    {
        String outputFile = outputFolder + File.separator + fileName;
        
        if (enableLogging)
        {
            logger.info("Объединяем чанки в файл: " + outputFile);
        }
        
        try (FileOutputStream output = new FileOutputStream(outputFile))
        {
            for (int i = 0; i < numberOfChunks; i++)
            {
                String chunkFile = outputFolder + File.separator + fileName + ".chunk_" + i;
                Path chunkPath = Paths.get(chunkFile);
                
                if (Files.exists(chunkPath))
                {
                    long chunkSize = Files.size(chunkPath);
                    Files.copy(chunkPath, output);
                    
                    if (enableLogging)
                    {
                        logger.fine("Добавлен чанк " + i + " размером " + chunkSize + " байт");
                    }
                }
                else
                {
                    if (enableLogging)
                    {
                        logger.warning("Чанк " + i + " не найден: " + chunkFile);
                    }
                }
            }
            
            if (enableLogging)
            {
                logger.info("Файл успешно объединен: " + outputFile);
            }
            return true;
        }
        catch (Exception e)
        {
            String errorMsg = "Не смог объединить чанки: " + e.getMessage();
            System.out.println(errorMsg);
            if (enableLogging)
            {
                logger.severe(errorMsg);
            }
            return false;
        }
    }
    
    private void cleanupTempFiles()
    {
        if (enableLogging)
        {
            logger.info("Очищаем временные файлы для " + fileName);
        }
        
        int deletedCount = 0;
        for (int i = 0; i < numberOfChunks; i++)
        {
            String chunkFile = outputFolder + File.separator + fileName + ".chunk_" + i;
            File file = new File(chunkFile);
            if (file.exists())
            {
                if (file.delete())
                {
                    deletedCount++;
                    if (enableLogging)
                    {
                        logger.fine("Удален временный файл: " + chunkFile);
                    }
                }
                else
                {
                    if (enableLogging)
                    {
                        logger.warning("Не удалось удалить временный файл: " + chunkFile);
                    }
                }
            }
        }
        
        String cleanupMsg = "Очищено " + deletedCount + " временных файлов для " + fileName;
        System.out.println(cleanupMsg);
        if (enableLogging)
        {
            logger.info(cleanupMsg);
        }
    }
}