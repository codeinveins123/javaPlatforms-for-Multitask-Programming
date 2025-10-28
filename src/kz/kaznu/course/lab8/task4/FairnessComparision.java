package kz.kaznu.course.lab8.task4;

import java.util.concurrent.locks.ReentrantLock;

public class FairnessComparision
{
    /**
     * Тестирует работу блокировки с указанным режимом fairness
     *
     * @param fair true для справедливой блокировки, false для обычной
     *
     * ЦЕЛЬ МЕТОДА: Продемонстрировать разницу в порядке получения блокировки
     * между fair и non-fair режимами
     */
    public static void testFairness(boolean fair) throws InterruptedException
    {
        System.out.println("\n" + (fair ? "FAIR" : "NON-FAIR") + " режим:");
        System.out.println("=".repeat(50));

        ReentrantLock lock = new ReentrantLock(fair);

        int threadCount = 5;
        Thread[] threads = new Thread[threadCount];
        for (int i = 1; i <= threadCount; i++)
        {
            final int threadId = i;
            threads[i - 1] = new Thread(() -> {
                for (int attempt = 1; attempt <= 3; attempt++)
                {
                    System.out.println("Thread-" + threadId + " запрашивает ресурс (попытка " + attempt + ")");
                    lock.lock();
                    try
                    {
                        System.out.println("Thread-" + threadId + " получил ресурс");
                        Thread.sleep(100); // имитация работы
                    }
                    catch (InterruptedException e)
                    {
                        Thread.currentThread().interrupt();
                    }
                    finally
                    {
                        System.out.println("Thread-" + threadId + " освободил ресурс");
                        lock.unlock();
                    }
                }
            }, "Thread-" + i);
        }

        for (Thread t : threads)
        {
            t.start();
        }
            
        for (Thread t : threads)
        {
            t.join();
        }
    }

    public static void main(String[] args) throws InterruptedException
    {
        // Тестируем non-fair режим
        testFairness(false);

        Thread.sleep(1000);

        // Тестируем fair режим
        testFairness(true);
    }
}