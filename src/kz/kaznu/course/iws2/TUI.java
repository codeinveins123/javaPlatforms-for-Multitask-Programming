package kz.kaznu.course.iws2;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class TUI
{
    private AtomicBoolean running;
    
    public TUI()
    {
        running = new AtomicBoolean(true);
    }
    
    public void displayUI(Map<String, ProgressTracker> progressTrackers)
    {
        while(running.get())
        {
            try
            {
                Thread.sleep(1000);
                clearConsole();
                printHeader();
                printDownloads(progressTrackers);
            }
            catch(InterruptedException e)
            {
                break;
            }
        }
    }
    
    private void clearConsole()
    {
        try
        {
            if(System.getProperty("os.name").contains("Windows"))
            {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else
            {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        }
        catch(Exception e)
        {
            for(int i = 0; i < 50; i++)
            {
                System.out.println();
            }
        }
    }
    
    private void printHeader()
    {
        System.out.println("==========================================");
        System.out.println("         МЕНЕДЖЕР ЗАГРУЗКИ ФАЙЛОВ         ");
        System.out.println("==========================================");
        System.out.println();
    }
    
    private void printDownloads(Map<String, ProgressTracker> trackers)
    {
        if(trackers.isEmpty())
        {
            System.out.println("Нет активных загрузок");
            return;
        }
        
        int index = 1;
        for(ProgressTracker tracker : trackers.values())
        {
            System.out.println("[" + index++ + "] " + tracker.getFileName());
            System.out.println("    Прогресс: " + tracker.getFormattedProgress());
            
            String validationInfo = tracker.getValidationInfo();
            if(!validationInfo.isEmpty())
            {
                System.out.println("    " + validationInfo);
            }
            
            System.out.println();
        }
        
        System.out.println("==========================================");
        System.out.println("Нажмите Ctrl+C для остановки");
    }
    
    public void stop()
    {
        running.set(false);
    }
}