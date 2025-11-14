package kz.kaznu.course.lab11.task1;

import java.util.concurrent.Semaphore;;

public class PrinterManager
{
    private static final int printerCount = 3;

    //Используем семафор потому что только 3 принтера
    //То есть ограничение на количество одновременных использований
    private final Semaphore semaphore = new Semaphore(printerCount, true);

    public void usePrinter(String personName)
    {
        try
        {   
            System.out.println(personName + " is waiting for the printer");
            semaphore.acquire();
            System.out.println(personName + " is using the printer");
            Thread.sleep(1000);
            System.out.println(personName + " finished using the printer");
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        finally
        {
            semaphore.release();
        }
    }
}
