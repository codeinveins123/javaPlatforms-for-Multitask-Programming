package kz.kaznu.course.lab11.task1;

import java.util.Random;

public class Main
{
    public static void main(String[] args)
    {
        PrinterManager printerManager = new PrinterManager();
        
        int personCount = new Random().nextInt(10) + 5;
        Thread[] threads = new Thread[personCount];
        for(int i = 0; i < personCount; i++)
        {
            int number = i;
            threads[i] = new Thread(() -> printerManager.usePrinter("Person-" + number));
            threads[i].start();
        }

        for(int i = 0; i < personCount; i++)
        {
            try
            {
                threads[i].join();
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}
