package kz.kaznu.course.iws2;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ProgressTracker
{
    private String url;
    private long fileSize;
    private AtomicLong downloadedBytes;
    private long startTime;
    private String status;
    private Map<Integer, Long> chunkProgress;
    private boolean validationSuccess;
    private String fileHash;
    private long expectedSize;
    
    public ProgressTracker(String url)
    {
        this.url = url;
        this.downloadedBytes = new AtomicLong(0);
        this.startTime = System.currentTimeMillis();
        this.status = "В очереди";
        this.chunkProgress = new ConcurrentHashMap<>();
    }
    
    public void setFileSize(long size)
    {
        this.fileSize = size;
    }
    
    public void updateProgress(int chunkNumber, long chunkBytes, long totalBytes)
    {
        chunkProgress.put(chunkNumber, chunkBytes);
        downloadedBytes.set(totalBytes);
    }
    
    public void setStatus(String status)
    {
        this.status = status;
    }
    
    public void setValidationResult(boolean success, String hash, long expectedSize)
    {
        this.validationSuccess = success;
        this.fileHash = hash;
        this.expectedSize = expectedSize;
    }
    
    public double getProgressPercentage()
    {
        if(fileSize <= 0) return 0;
        return (downloadedBytes.get() * 100.0) / fileSize;
    }
    
    public double getDownloadSpeed()
    {
        long elapsedTime = System.currentTimeMillis() - startTime;
        if(elapsedTime == 0) return 0;
        
        double speed = (downloadedBytes.get() * 1000.0) / elapsedTime; // bytes per second
        return speed / (1024 * 1024); // MB/s
    }
    
    public String getStatus()
    {
        return status;
    }
    
    public String getFileName()
    {
        int lastSlash = url.lastIndexOf('/');
        if(lastSlash != -1 && lastSlash < url.length() - 1)
        {
            return url.substring(lastSlash + 1);
        }
        return url;
    }
    
    public String getFormattedProgress()
    {
        double percentage = getProgressPercentage();
        double speed = getDownloadSpeed();
        long downloaded = downloadedBytes.get();
        long total = fileSize;
        
        String downloadedStr = formatBytes(downloaded);
        String totalStr = formatBytes(total);
        
        return String.format("%.1f%% (%s/%s) %.2f MB/s - %s", 
            percentage, downloadedStr, totalStr, speed, status);
    }
    
    public String getValidationInfo()
    {
        if(fileHash == null) return "";
        
        String sizeStatus = validationSuccess ? "OK" : "FAILED";
        String hashShort = fileHash.length() > 16 ? 
            fileHash.substring(0, 16) + "..." : fileHash;
            
        return String.format("Размер: %s | Хеш(SHA-256): %s", sizeStatus, hashShort);
    }
    
    private String formatBytes(long bytes)
    {
        if(bytes < 1024) return bytes + " B";
        if(bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if(bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}