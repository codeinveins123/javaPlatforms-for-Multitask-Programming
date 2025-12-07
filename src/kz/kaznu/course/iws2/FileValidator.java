package kz.kaznu.course.iws2;

import java.net.URL;
import java.net.URI;
import java.net.HttpURLConnection;

public class FileValidator
{

    private FileValidator()
    {
    }

    public static HttpURLConnection openConnectionIfValid(String urlStr)
    {
        try
        {
            URL url = new URI(urlStr).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // или HEAD, если только проверка
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == 200)
            {
                return conn;
            }
        } 
        catch (Exception e)
        {
            System.out.println("Ошибка с URL: " + e.getMessage());
        }
        return null;
    }
}
