package kz.kaznu.course.lab11.task2;

import java.util.concurrent.CountDownLatch;
import java.util.Random;

public class DataLoader implements Runnable
{

    //Используем countDownLatch для того, чтобы главный поток
    //Знал, что все потоки загрузили данные
    private String dataName;
    private final CountDownLatch countDownLatch;
    
    public DataLoader(String dataName, CountDownLatch countDownLatch)
    {
        this.dataName = dataName;
        this.countDownLatch = countDownLatch;
    }
    
    @Override
    public void run()
    {
        System.out.println(dataName + " is loading");
        try
        {
            Thread.sleep((new Random().nextInt(1, 5) * 1000));
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        System.out.println(dataName + " is loaded");
        countDownLatch.countDown();
    }
}
