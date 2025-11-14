package kz.kaznu.course.lab11.task2;

import java.util.concurrent.CountDownLatch;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        int dataCount = 4;
        CountDownLatch latch = new CountDownLatch(dataCount);

        System.out.println("!!!======== Application is loading data  ===========!!!");
        new Thread(new DataLoader("Data Base", latch)).start();
        new Thread(new DataLoader("Config", latch)).start();
        new Thread(new DataLoader("Cache", latch)).start();
        new Thread(new DataLoader("API", latch)).start();

        //Задерживаю главный поток для того, чтобы успели инициализироваться все потоки
        Thread.sleep(1000);

        System.out.println("!!!====== Application is waiting for data to be loaded  ===========!!!");
        latch.await();
        System.out.println("!!!======= Application is ready  ===========!!!");
    }
}