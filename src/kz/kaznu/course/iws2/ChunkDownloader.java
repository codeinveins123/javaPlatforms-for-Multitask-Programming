package kz.kaznu.course.iws2;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ChunkDownloader
{
    private HttpURLConnection conn;
    private long fileSize;
    private ExecutorService executor;
    private CountDownLatch latch;
    private String outputFolder;
    private String fileName;

    public ChunkDownloader(HttpURLConnection conn, String outputFolder, String fileName)
    {
        this.conn = conn;
        this.fileSize = conn.getContentLengthLong();
        this.outputFolder = outputFolder;
        this.fileName = fileName;
    }

    public void download()
    {
        int numberChunks = 8;
        long chunkSize = fileSize / numberChunks;

        executor = Executors.newFixedThreadPool(numberChunks);
        latch    = new CountDownLatch(numberChunks);

        for(int i = 1; i <= numberChunks; i++)
        {
            final int chunkNumber = i;
            final long startByte = (chunkNumber - 1) * chunkSize;
            final long endByte = (chunkNumber == numberChunks) ? fileSize - 1 : chunkNumber * chunkSize - 1;
            executor.submit(() ->
            {
                try
                {
                    downloadChunk(chunkNumber, startByte, endByte);
                }
                catch (Exception e)
                {
                    System.out.println("Error downloading chunk: " + e.getMessage());
                }
                finally
                {
                    latch.countDown();
                }
            });
        }
        try
        {
            latch.await();
            executor.shutdown();
        }
        catch(InterruptedException e)
        {
            System.out.println("Error downloading chunks: " + e.getMessage());
        }
    }

    private void downloadChunk(int chunkNumber, long startByte, long endByte)
    {
        try
        {
            URL url = conn.getURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Настройки HTTP-запроса
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_PARTIAL && responseCode != HttpURLConnection.HTTP_OK)
            {
                System.out.println("Chunk " + chunkNumber + " failed: HTTP " + responseCode);
                connection.disconnect();
                return;
            }

            // Имя временного файла чанка
            String chunkFile = outputFolder + "/chunk_" + chunkNumber + ".tmp";

            // Копирование байт из потока в файл
            try (InputStream in = connection.getInputStream();
                FileOutputStream out = new FileOutputStream(chunkFile))
            {
                byte[] buffer = new byte[8192];
                int bytesRead;
                long totalBytes = 0;

                while ((bytesRead = in.read(buffer)) != -1)
                {
                    out.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }

                out.flush();
                System.out.println("Chunk " + chunkNumber + " downloaded: " + totalBytes + " bytes (" +
                                startByte + "-" + endByte + ")");
            }

            connection.disconnect();
        }
        catch (Exception e)
        {
            System.out.println("Error downloading chunk " + chunkNumber + ": " + e.getMessage());
        }
    }

    private  void mergeChunks(String fileName, String outputFolder)
    {
        try (FileOutputStream output = new FileOutputStream(fileName))
        {
            for (int i = 0; i < numberOfChunks; i++)
            {
                Files.copy(Paths.get("chunk_" + i + ".tmp"), output);
            }
        }
    }
}
