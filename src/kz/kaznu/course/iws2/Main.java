package kz.kaznu.course.iws2;

import java.util.logging.*;
import java.io.File;

//Для компиляции:
//javac -d bin -cp src src/kz/kaznu/course/iws2/*.java

//Для запуска:
//java -cp bin kz.kaznu.course.iws2.Main --files

//Для тестов:
//https://people.sc.fsu.edu/~jburkardt/data/csv/hw_200.csv
//https://people.sc.fsu.edu/~jburkardt/data/csv/hw_25000.csv
//https://cdn.wallpapersafari.com/32/53/bGUmIh.png
//https://cdn.wallpapersafari.com/83/65/rI1ed9.jpg
//http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4 

public class Main
{
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    
    public static void main(String[] args)
    {
        setupLogging();
        
        if (args.length > 0 && args[0].equals("--help"))
        {
            printHelp();
            return;
        }

        if (args.length == 0)
        {
            printHelp();
            return;
        }
        
        DownloadManager downloadManager = new DownloadManager();
        String[] urls = null;
        String outputFolder = "Download";
        int parallelFiles = 2;
        int chunkThreads = 4;
        int chunkSizeMB = 1;
        boolean enableLogging = false;
        
        for (int i = 0; i < args.length; i++)
        {
            switch(args[i])
            {
                case "--files":
                    int endIndex = i + 1;
                    while (endIndex < args.length && !args[endIndex].startsWith("--"))
                    {
                        endIndex++;
                    }
                    urls = new String[endIndex - i - 1];
                    System.arraycopy(args, i + 1, urls, 0, urls.length);
                    i = endIndex - 1;
                    break;
                    
                case "--output-folder":
                    outputFolder = args[++i];
                    break;
                    
                case "--parallel-files":
                    parallelFiles = Integer.parseInt(args[++i]);
                    break;
                    
                case "--chunk-threads":
                    chunkThreads = Integer.parseInt(args[++i]);
                    break;
                    
                case "--chunk-size":
                    chunkSizeMB = Integer.parseInt(args[++i]);
                    break;
                    
                case "--log":
                    enableLogging = true;
                    break;
                    
                default:
                    if (i == 0 && !args[i].startsWith("--") && args.length >= 3)
                    {
                        urls = new String[]{args[0]};
                        outputFolder = args[1];
                        String fileName = args[2];
                        System.setProperty("defaultFileName", fileName);
                        i = 2;
                    }
                    break;
            }
        }
        
        if (urls == null || urls.length == 0)
        {
            System.out.println("Ошибка: не указаны URL файлов для загрузки");
            printHelp();
            return;
        }
        
        if (enableLogging)
        {
            setupFileLogging(outputFolder);
            logger.info("Начинаем загрузку " + urls.length + " файлов");
        }
        
        downloadManager.setOutputFolder(outputFolder);
        downloadManager.setMaxConcurrentDownloads(parallelFiles);
        downloadManager.setChunkThreads(chunkThreads);
        downloadManager.setChunkSizeMB(chunkSizeMB);
        downloadManager.setEnableLogging(enableLogging);
        
        for (String url : urls)
        {
            downloadManager.addDownload(url);
            if (enableLogging)
            {
                logger.info("Добавлена задача на загрузку: " + url);
            }
        }
        
        downloadManager.startDownloads();
    }
    
    private static void setupLogging()
    {
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers.length > 0 && handlers[0] instanceof ConsoleHandler)
        {
            rootLogger.removeHandler(handlers[0]);
        }
    }
    
    private static void setupFileLogging(String outputFolder)
    {
        try
        {
            File logDir = new File(outputFolder);
            if (!logDir.exists())
            {
                logDir.mkdirs();
            }
            
            String timestamp = new java.text.SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new java.util.Date());
            String logFileName = outputFolder + File.separator + "download_" + timestamp + ".log";
            
            FileHandler fileHandler = new FileHandler(logFileName);
            fileHandler.setFormatter(new SimpleFormatter());
            
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            
            System.out.println("Логирование включено. Лог файл: " + logFileName);
        }
        catch (Exception e)
        {
            System.err.println("Не удалось настроить логирование: " + e.getMessage());
        }
    }
    
    private static void printHelp()
    {
        System.out.println("Использование:");
        System.out.println("  Для нескольких файлов:");
        System.out.println("    java Main --files <URL1> <URL2> ... [опции]");
        System.out.println("  Для одного файла (старый формат):");
        System.out.println("    java Main <URL> <OutputFolder> <FileName>");
        System.out.println();
        System.out.println("Опции:");
        System.out.println("  --output-folder <path>    Папка для сохранения (по умолчанию: Download)");
        System.out.println("  --parallel-files <N>      Количество одновременно загружаемых файлов (по умолчанию: 2)");
        System.out.println("  --chunk-threads <M>       Количество потоков для загрузки одного файла (по умолчанию: 4)");
        System.out.println("  --chunk-size <S>          Размер chunk в мегабайтах (по умолчанию: 1)");
        System.out.println("  --log                     Включить логирование в файл");
        System.out.println("  --help                    Показать эту справку");
        System.out.println();
        System.out.println("Примеры:");
        System.out.println("  java Main --files https://example.com/file1.zip --output-folder Downloads --log");
        System.out.println("  java Main --files file1.zip file2.iso --parallel-files 3 --chunk-threads 8 --chunk-size 2 --log");
    }
}