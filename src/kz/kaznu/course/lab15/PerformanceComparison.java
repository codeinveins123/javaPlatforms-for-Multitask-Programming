package kz.kaznu.course.lab15;

import java.util.concurrent.*;

public class PerformanceComparison
{
    private static final int THREAD_COUNT = 10;
    private static final int TASKS_PER_THREAD = 1000;

    public static void main(String[] args) throws Exception
    {
        System.out.println("╔══════════════════════════════════════════════╗");
        System.out.println("║         СРАВНЕНИЕ ПРОИЗВОДИТЕЛЬНОСТИ         ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        System.out.println("--- Проблемная версия (synchronized) ---");
        long timeSynchronized = testProcessor(new TaskProcessor());

        System.out.println("\n--- Оптимизированная версия ---");
        long timeOptimized = testProcessor(new TaskProcessorOptimized());

        System.out.println("\n═══════════════════════════════════════════════");
        System.out.println("РЕЗУЛЬТАТЫ:");
        System.out.printf("Проблемная версия: %d мс%n", timeSynchronized);
        System.out.printf("Оптимизированная: %d мс%n", timeOptimized);
        System.out.printf("Ускорение: %.2fx%n", (double) timeSynchronized / timeOptimized);
        System.out.println("═══════════════════════════════════════════════");
    }

    // TODO: Универсальный тест производительности
    private static long testProcessor(Object processor) throws Exception
    {
        long start = System.currentTimeMillis();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        // Фаза 1: добавление задач
        for (int i = 0; i < THREAD_COUNT; i++)
        {
            int t = i;
            executor.submit(() ->
            {
                for (int j = 0; j < TASKS_PER_THREAD; j++)
                {
                    Task task = new Task("task-" + t + "-" + j, "data-" + j, j % 10);
                    if (processor instanceof TaskProcessor)
                        ((TaskProcessor) processor).addTask(task);
                    else
                        ((TaskProcessorOptimized) processor).addTask(task);
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        // Фаза 2: обработка задач
        ExecutorService exec2 = Executors.newFixedThreadPool(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; i++)
        {
            exec2.submit(() ->
            {
                Task task;
                while (true)
                {
                    if (processor instanceof TaskProcessor)
                        task = ((TaskProcessor) processor).getNextTask();
                    else
                        task = ((TaskProcessorOptimized) processor).getNextTask();

                    if (task == null)
                        break;

                    if (processor instanceof TaskProcessor)
                        ((TaskProcessor) processor).processTask(task);
                    else
                        ((TaskProcessorOptimized) processor).processTask(task);
                }
            });
        }

        exec2.shutdown();
        exec2.awaitTermination(10, TimeUnit.MINUTES);

        return System.currentTimeMillis() - start;
    }
}
