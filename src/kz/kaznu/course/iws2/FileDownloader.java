package kz.kaznu.course.iws2;

import java.net.HttpURLConnection;
import java.util.logging.Logger;

public class FileDownloader
{
    private static final Logger logger = Logger.getLogger(FileDownloader.class.getName());
    
    private String url;
    private String outputFolder;
    private String fileName;
    private int chunkThreads;
    private int chunkSizeMB;
    private ProgressTracker progressTracker;
    private long fileSize;
    private boolean enableLogging = false;
    private volatile boolean running = true;
    
    public FileDownloader(String url, String outputFolder, String fileName)
    {
        this(url, outputFolder, fileName, 4, 1);
    }
    
    public FileDownloader(String url, String outputFolder, String fileName, 
                         int chunkThreads, int chunkSizeMB)
    {
        this.url = url;
        this.outputFolder = outputFolder;
        this.fileName = fileName;
        this.chunkThreads = chunkThreads;
        this.chunkSizeMB = chunkSizeMB;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public String getOutputFolder()
    {
        return outputFolder;
    }
    
    public String getFileName()
    {
        return fileName;
    }
    
    public void setProgressTracker(ProgressTracker tracker)
    {
        this.progressTracker = tracker;
    }
    
    public void setEnableLogging(boolean enable)
    {
        this.enableLogging = enable;
    }
    
    public long getFileSize()
    {
        return fileSize;
    }
    
    public void stop()
    {
        running = false;
        if (enableLogging)
        {
            logger.info("Получена команда остановки для загрузчика: " + fileName);
        }
    }
    
    public boolean isRunning()
    {
        return running;
    }
    
    public boolean downloadFile()
    {
        if (!running)
        {
            if (enableLogging)
            {
                logger.info("Загрузка отменена перед началом: " + fileName);
            }
            if (progressTracker != null)
            {
                progressTracker.setStatus("Отменено");
            }
            return false;
        }
        
        if (enableLogging)
        {
            logger.info("Начинаем загрузку файла: " + fileName + " из " + url);
        }
        
        HttpURLConnection conn = FileValidator.openConnectionIfValid(url);
        if (conn == null)
        {
            String errorMsg = "Ошибка: Нет соединения или неверный URL: " + url;
            System.out.println(errorMsg);
            if (enableLogging)
            {
                logger.warning(errorMsg);
            }
            if (progressTracker != null)
            {
                progressTracker.setStatus("Ошибка: неверный URL");
            }
            return false;
        }
        
        try
        {
            fileSize = conn.getContentLengthLong();
            
            if (enableLogging)
            {
                logger.info("Размер файла " + fileName + ": " + fileSize + " байт");
            }
            
            int numberOfChunks = calculateNumberOfChunks(fileSize);
            
            if (enableLogging)
            {
                logger.info("Используем " + numberOfChunks + " потоков для загрузки файла " + fileName);
            }
            
            ChunkDownloader chunkDownloader = new ChunkDownloader(
                conn, outputFolder, fileName, numberOfChunks, progressTracker
            );
            chunkDownloader.setEnableLogging(enableLogging);
            
            boolean success = chunkDownloader.download();
            conn.disconnect();
            
            if (enableLogging)
            {
                if (success)
                {
                    logger.info("Файл " + fileName + " успешно загружен");
                }
                else
                {
                    logger.warning("Не удалось загрузить файл " + fileName);
                }
            }
            
            return success;
        }
        catch (Exception e)
        {
            String errorMsg = "Ошибка при загрузке " + fileName + ": " + e.getMessage();
            System.out.println(errorMsg);
            if (enableLogging)
            {
                logger.severe(errorMsg);
            }
            if (progressTracker != null)
            {
                progressTracker.setStatus("Ошибка: " + e.getMessage());
            }
            return false;
        }
    }
    
    private int calculateNumberOfChunks(long fileSize)
    {
        long chunkSizeBytes = chunkSizeMB * 1024 * 1024;
        
        if (fileSize <= chunkSizeBytes)
        {
            return 1;
        }
        
        int calculatedChunks = (int)(fileSize / chunkSizeBytes);
        if (fileSize % chunkSizeBytes != 0)
        {
            calculatedChunks++;
        }
        
        return Math.min(calculatedChunks, chunkThreads);
    }
}