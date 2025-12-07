package kz.kaznu.course.iws2;

import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader
{
    private String url;
    private HttpURLConnection conn;
    private String outputFolder;
    private String fileName;

    public FileDownloader(String url, String outputFolder, String fileName)
    {
        this.url = url;
        this.outputFolder = outputFolder;
        this.fileName = fileName;
    }

    public void downloadFile()
    {
        conn = FileValidator.openConnectionIfValid(url);
        if(conn == null)
        {
            System.out.println("Error invalid url");
            return;
        }

        ChunkDownloader chunkDownloader = new ChunkDownloader(conn, outputFolder, fileName);
        chunkDownloader.download();
    }
    
}