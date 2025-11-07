package kz.kaznu.course.lab10.task3;

import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class Main
{
    public static void main(String[] args)
    {
        ExplicitLockCounter explicitLockCounter = new ExplicitLockCounter();
        SynchronizedMapCounter synchronizedMapCounter = new SynchronizedMapCounter();
        ConcurrentMapCounter concurrentMapCounter = new ConcurrentMapCounter();

        String[] words = IntStream.range(0, 100).mapToObj(i -> "w" + i).toArray(String[]::new);
        int threads = 10;
        int operations = 1000_000;

        long syncMapMs = test(synchronizedMapCounter::count, words, threads, operations);
        long concMapMs = test(concurrentMapCounter::count, words, threads, operations);
        long lockMapMs = test(explicitLockCounter::count, words, threads, operations);

        System.out.println("SynchronizedMap: " + syncMapMs + " ms");
        System.out.println("ConcurrentHashMap: " + concMapMs + " ms");
        System.out.println("ExplicitLock: " + lockMapMs + " ms");
        String[] names = {"SynchronizedMap", "ConcurrentHashMap", "ExplicitLock"};
        long[] times = {syncMapMs, concMapMs, lockMapMs};

        int best = 0, second = 1;
        if (times[1] < times[0])
        {
            best = 1;
            second = 0;
        }
        if (times[2] < times[best])
        {
            second = best;
            best = 2;
        }
        else if (times[2] < times[second])
        {
            second = 2;
        }

        String winnerName = names[best];
        long winnerTime = times[best];
        String secondBestName = names[second];
        long secondBestTime = times[second];

        double speed = (double) secondBestTime / winnerTime;
        System.out.println("Winner: " + winnerName + " (в " + String.format(Locale.US, "%.2f", speed) + " раза быстрее " + secondBestName + ")");
    }

    private static long test(Consumer<String> counter, String[] words, int threadCount, int operations)
    {
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);

        Thread[] threads = new Thread[threadCount];
        for (int t = 0; t < threadCount; t++)
        {
            threads[t] = new Thread(() -> {
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                try 
                {
                    start.await();
                } 
                catch(InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                
                }
                for (int i = 0; i < operations; i++)
                {
                    String w = words[rand.nextInt(words.length)];
                    counter.accept(w);
                }
                done.countDown();
            });
            threads[t].start();
        }

        long timeStart = System.nanoTime();
        start.countDown();
        try 
        {
            done.await();
        }
        catch (InterruptedException e)
        { 
            Thread.currentThread().interrupt();
        }
        long timeEnd = System.nanoTime();
        return TimeUnit.NANOSECONDS.toMillis(timeEnd - timeStart);
    }
}
