package kz.kaznu.course.iws2;

import java.net.URL;
import java.net.URI;
import java.net.HttpURLConnection;
import java.io.*;
import java.security.*;

public class FileValidator
{
    private FileValidator() {}
    
    public static HttpURLConnection openConnectionIfValid(String urlStr)
    {
        try
        {
            URL url = new URI(urlStr).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            
            if(conn.getResponseCode() == 200)
            {
                return conn;
            }
        }
        catch(Exception e)
        {
            System.out.println("Ошибка с URL: " + e.getMessage());
        }
        return null;
    }
    
    public static boolean validateSize(File file, long expectedSize)
    {
        long actualSize = file.length();
        return actualSize == expectedSize;
    }
    
    public static String calculateHash(File file, String algorithm)
    {
        try(FileInputStream fis = new FileInputStream(file))
        {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[8192];
            int bytesRead;
            
            while((bytesRead = fis.read(buffer)) != -1)
            {
                digest.update(buffer, 0, bytesRead);
            }
            
            byte[] hashBytes = digest.digest();
            return bytesToHex(hashBytes);
        }
        catch(Exception e)
        {
            System.out.println("Ошибка при вычислении хеша: " + e.getMessage());
            return "ERROR";
        }
    }
    
    private static String bytesToHex(byte[] bytes)
    {
        StringBuilder hexString = new StringBuilder();
        for(byte b : bytes)
        {
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}