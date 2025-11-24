package kz.kaznu.course.lab12.task2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RecursiveAction;

public class FileSearchTask extends RecursiveAction 
{
    private final File directory;
    private final String extension;
    private final List<String> results;
    private int counter;

    public FileSearchTask(File directory, String extension, List<String> results)
    {
        this.directory = directory;
        this.extension = extension;
        this.results = results;
        this.counter = 0;
    }

    @Override
    protected void compute()
    {
        File[] files = directory.listFiles();
        if (files == null)
        {
            return;
        }
        List<FileSearchTask> subTasks = new ArrayList<>();

        for (File file : files)
        {
            if (file.isDirectory())
            {
                FileSearchTask task = new FileSearchTask(file, extension, results);
                subTasks.add(task);
                task.fork();
            }
            else if (file.getName().endsWith(extension))
            {                
                results.add(file.getAbsolutePath());
            }
        }

        for (FileSearchTask task : subTasks)
        {
            task.join();
        }
    }

    public void printResults()
    {
        //Оставил себе коды цветов.
        String WHITE_BG  = "\u001B[47m";
        String RED_BG = "\u001B[41m";
        String BLACK = "\u001B[90m";
        String CYAN = "\u001B[36m";
        String GREEN = "\u001B[32m"; 
        String RESET = "\u001B[0m"; 
        String BOLD = "\u001B[1m";
        String YELLOW = "\u001B[33m";     
        for(String result : results)
        {
            int i = result.indexOf("Folder");
            if (i == -1)
            {
                i = 0;
            }

            String printStr = result.substring(i);
            System.out.printf("%s%s%2d.  %s%s\n", BOLD, CYAN, ++counter, GREEN, printStr, RESET);

        }
    }

    public int getCounter()
    {
        return counter;
    }
}
