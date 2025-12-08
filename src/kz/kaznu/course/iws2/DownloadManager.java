package kz.kaznu.course.iws2;

import java.util.concurrent.*;
import java.util.*;
import java.io.File;
import java.util.logging.Logger;

public class DownloadManager
{
    private static final Logger logger = Logger.getLogger(DownloadManager.class.getName());
    
    private ConcurrentHashMap<String, FileDownloader> downloaders;
    private ConcurrentHashMap<String, ProgressTracker> progressTrackers;
    private ExecutorService executorService;
    private String outputFolder;
    private int maxConcurrentDownloads = 2;
    private int chunkThreads = 4;
    private int chunkSizeMB = 1;
    private TUI tui;
    private boolean enableLogging = false;
    
    public DownloadManager()
    {
        downloaders = new ConcurrentHashMap<>();
        progressTrackers = new ConcurrentHashMap<>();
        tui = new TUI();
    }
    
    public void setOutputFolder(String folder)
    {
        this.outputFolder = folder;
        File dir = new File(folder);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
    }
    
    public void setMaxConcurrentDownloads(int max)
    {
        this.maxConcurrentDownloads = max;
    }
    
    public void setChunkThreads(int threads)
    {
        this.chunkThreads = threads;
    }
    
    public void setChunkSizeMB(int sizeMB)
    {
        this.chunkSizeMB = sizeMB;
    }
    
    public void setEnableLogging(boolean enable)
    {
        this.enableLogging = enable;
        if (enable)
        {
            logger.info("Логирование включено для DownloadManager");
        }
    }
    
    public void addDownload(String url)
    {
        String fileName = extractFileNameFromUrl(url);
        FileDownloader downloader = new FileDownloader(
            url, 
            outputFolder, 
            fileName, 
            chunkThreads, 
            chunkSizeMB
        );
        
        ProgressTracker tracker = new ProgressTracker(url);
        downloader.setProgressTracker(tracker);
        downloader.setEnableLogging(enableLogging);
        
        downloaders.put(url, downloader);
        progressTrackers.put(url, tracker);
        
        if (enableLogging)
        {
            logger.info("Добавлен загрузчик для файла: " + fileName + " из " + url);
        }
    }
    
    public void startDownloads()
    {
        if (enableLogging)
        {
            logger.info("Начинаем загрузку. Файлов: " + downloaders.size());
            logger.info("Параллельных загрузок: " + maxConcurrentDownloads);
            logger.info("Потоков на файл: " + chunkThreads);
            logger.info("Размер чанка: " + chunkSizeMB + " MB");
        }
        
        executorService = Executors.newFixedThreadPool(maxConcurrentDownloads);
        
        Thread uiThread = new Thread(() -> tui.displayUI(progressTrackers));
        uiThread.setDaemon(true);
        uiThread.start();
        
        for (String url : downloaders.keySet())
        {
            executorService.submit(() -> downloadFile(url));
        }
    }
    
    private void downloadFile(String url)
    {
        FileDownloader downloader = downloaders.get(url);
        ProgressTracker tracker = progressTrackers.get(url);
        
        if (downloader == null || tracker == null)
        {
            if (enableLogging)
            {
                logger.warning("Загрузчик или трекер не найден для URL: " + url);
            }
            return;
        }
        
        tracker.setStatus("Подготовка к загрузке...");
        
        if (enableLogging)
        {
            logger.info("Начинается загрузка: " + url);
        }
        
        try
        {
            tracker.setStatus("Загрузка...");
            
            boolean success = downloader.downloadFile();
            
            if (success)
            {
                if (enableLogging)
                {
                    logger.info("Загрузка успешна: " + downloader.getFileName());
                }
                
                tracker.setStatus("Валидация...");
                
                long expectedSize = downloader.getFileSize();
                String fullPath = downloader.getOutputFolder() + File.separator + downloader.getFileName();
                File downloadedFile = new File(fullPath);
                
                boolean sizeValid = FileValidator.validateSize(downloadedFile, expectedSize);
                String hash = FileValidator.calculateHash(downloadedFile, "SHA-256");
                
                tracker.setValidationResult(sizeValid, hash, expectedSize);
                tracker.setStatus(sizeValid ? "Завершено" : "Ошибка валидации");
                
                if (!sizeValid)
                {
                    String msg = "Размер загруженного файла не совпадает с ожидаемым! Файл: " + downloader.getFileName();
                    System.out.println("Предупреждение: " + msg);
                    if (enableLogging)
                    {
                        logger.warning(msg);
                    }
                }
                else
                {
                    if (enableLogging)
                    {
                        logger.info("Валидация успешна для файла: " + downloader.getFileName() + 
                                  " | Размер: " + expectedSize + " | Хеш: " + 
                                  (hash != null && hash.length() > 16 ? hash.substring(0, 16) + "..." : hash));
                    }
                }
            }
            else
            {
                tracker.setStatus("Ошибка загрузки");
                if (enableLogging)
                {
                    logger.warning("Загрузка провалена: " + downloader.getFileName());
                }
            }
        }
        catch (Exception e)
        {
            tracker.setStatus("Ошибка: " + e.getMessage());
            String errorMsg = "Ошибка при загрузке " + url + ": " + e.getMessage();
            System.err.println(errorMsg);
            if (enableLogging)
            {
                logger.severe(errorMsg);
            }
        }
    }
    
    public void stopDownloads()
    {
        if (enableLogging)
        {
            logger.info("Останавливаем загрузки...");
        }
        
        executorService.shutdownNow();
        tui.stop();
        
        for (FileDownloader downloader : downloaders.values())
        {
            downloader.stop();
        }
        
        if (enableLogging)
        {
            logger.info("Все загрузки остановлены");
        }
    }
    
    public FileDownloader getDownloader(String url)
    {
        return downloaders.get(url);
    }
    
    public ProgressTracker getProgressTracker(String url)
    {
        return progressTrackers.get(url);
    }
    
    public Collection<FileDownloader> getAllDownloaders()
    {
        return downloaders.values();
    }
    
    public Collection<ProgressTracker> getAllProgressTrackers()
    {
        return progressTrackers.values();
    }
    
    private String extractFileNameFromUrl(String url)
    {
        String defaultName = System.getProperty("defaultFileName");
        if (defaultName != null)
        {
            System.clearProperty("defaultFileName");
            return defaultName;
        }
        
        try
        {
            int lastSlash = url.lastIndexOf('/');
            if (lastSlash != -1 && lastSlash < url.length() - 1)
            {
                return url.substring(lastSlash + 1);
            }
        }
        catch (Exception e)
        {
            if (enableLogging)
            {
                logger.warning("Не удалось извлечь имя файла из URL: " + url);
            }
        }
        
        return "file_" + Math.abs(url.hashCode());
    }
}