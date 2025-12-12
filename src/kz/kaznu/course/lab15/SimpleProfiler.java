package kz.kaznu.course.lab15;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SimpleProfiler
{
    public static void profileTaskProcessor()
    {
        System.out.println("=== ПРОФИЛИРОВАНИЕ TASK PROCESSOR ===\n");

        TaskProcessor processor = new TaskProcessor();
        int threadCount = 10;
        int tasksPerThread = 100;

        long addTasksTime = measureAddTasks(processor, threadCount, tasksPerThread);
        System.out.printf("Добавление %d задач: %d мс%n",
                threadCount * tasksPerThread, addTasksTime);

        long processTasksTime = measureProcessTasks(processor, threadCount);
        System.out.printf("Обработка задач: %d мс%n", processTasksTime);

        measureCpuVsWallTime(processor);

        System.out.println("\nСтатистика:");
        System.out.println("Обработано задач: " + processor.getProcessedCount());
    }

    private static long measureAddTasks(TaskProcessor processor,
                                        int threadCount,
                                        int tasksPerThread)
    {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++)
        {
            final int threadId = i;
            
            executor.submit(() ->
            {
                for (int j = 0; j < tasksPerThread; j++)
                {
                    Task task = new Task("task-" + threadId + "-" + j,
                            "data-" + threadId + "-" + j,
                            j % 10);
                    processor.addTask(task);
                }
                latch.countDown();
            });
        }

        try
        {
            latch.await(30, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        
        return endTime - startTime;
    }

    private static long measureProcessTasks(TaskProcessor processor,
                                            int threadCount)
    {
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < threadCount; i++)
        {
            executor.submit(() ->
            {
                Task task;
                
                while ((task = processor.getNextTask()) != null)
                {
                    processor.processTask(task);
                }
                
                latch.countDown();
            });
        }

        try
        {
            latch.await(60, TimeUnit.SECONDS);
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
        
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        
        return endTime - startTime;
    }

    private static void measureCpuVsWallTime(TaskProcessor processor)
    {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long threadId = Thread.currentThread().getId();
        
        long cpuStartTime = threadMXBean.getThreadCpuTime(threadId);
        long wallStartTime = System.nanoTime();

        for (int i = 0; i < 100; i++)
        {
            Task task = new Task("profile-" + i, "data", 1);
            processor.addTask(task);
            processor.processTask(task);
        }

        long cpuEndTime = threadMXBean.getThreadCpuTime(threadId);
        long wallEndTime = System.nanoTime();
        
        long cpuTimeMs = (cpuEndTime - cpuStartTime) / 1_000_000;
        long wallTimeMs = (wallEndTime - wallStartTime) / 1_000_000;
        
        System.out.printf("\nCPU Time vs Wall Time:\n");
        System.out.printf("CPU Time:  %d мс\n", cpuTimeMs);
        System.out.printf("Wall Time: %d мс\n", wallTimeMs);
        System.out.printf("Ratio: %.2f\n", (double) cpuTimeMs / wallTimeMs);
    }

    public static void main(String[] args)
    {
        profileTaskProcessor();
    }
}