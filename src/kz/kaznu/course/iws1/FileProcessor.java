package kz.kaznu.course.iws1;

import java.io.*;
import java.util.*;

public class FileProcessor implements Runnable
{
    private String filename;
    private List<SalesRecord> results;
    private volatile boolean completed;
    private volatile String errorMessage;
    
    public FileProcessor(String filename)
    {
        this.filename = filename;
        this.results = new ArrayList<>();
        this.completed = false;
        this.errorMessage = null;
    }
    
    @Override
    public void run()
    {
        readFile();
    }

    public void readFile()
    {
/*         try
        {
            Thread.sleep(10000);
        }
            catch (InterruptedException e)
        {
            e.printStackTrace(); // или Thread.currentThread().interrupt();
        } */
        System.out.println("[" + Thread.currentThread().getName() + "] Начало обработки: " + filename);
        long startTime = System.currentTimeMillis();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename)))
        {
            String line;
            boolean skipHeader = false;
            while((line = reader.readLine()) != null)
            {
                if(!skipHeader)
                {
                    skipHeader = true;
                    continue;
                }
                String[] parts = line.split(",");
                if(parts.length == 5)
                {
                    SalesRecord record = new SalesRecord(Integer.parseInt(parts[0]), 
                                                         parts[1], 
                                                         Integer.parseInt(parts[2]), 
                                                         Double.parseDouble(parts[3]),
                                                         parts[4]);
                    results.add(record);
                }
            }
            completed = true;
        }
        catch(IOException e)
        {
            errorMessage = "Error file reading: " + e.getMessage();
        }
        catch(NumberFormatException e)
        {
            errorMessage = "Error file data: " + e.getMessage();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("[" + Thread.currentThread().getName() + "] Обработано записей: " + 
                            results.size() + " за " + (endTime - startTime) + " мс");       
    }
    
    public List<SalesRecord> getResults()
    {
        return results;
    }
    
    public boolean isCompleted()
    {
        return completed;
    }
    
    public String getErrorMessage()
    {
        return errorMessage;
    }
    
    public String getFilename()
    {
        return filename;
    }
}
