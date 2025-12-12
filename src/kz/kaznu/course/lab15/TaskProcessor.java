package kz.kaznu.course.lab15;

import java.util.*;
import java.util.concurrent.*;

public class TaskProcessor
{
    private final List<Task> tasks;
    private final Map<String, String> cache;
    private int processedCount;

    public TaskProcessor()
    {
        this.tasks = new ArrayList<>();
        this.cache = new HashMap<>();
        this.processedCount = 0;
    }

    // ПРОБЛЕМА 1: Неэффективная синхронизация всего метода
    public synchronized void addTask(Task task)
    {
        tasks.add(task);

        // ПРОБЛЕМА 2: Тяжёлая операция в synchronized блоке
        try
        {
            Thread.sleep(3); // Симуляция записи в БД :)
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
        }
    }

    // ПРОБЛЕМА 3: Весь метод synchronized
    public synchronized Task getNextTask()
    {
        if (tasks.isEmpty())
        {
            return null;
        }

        // ПРОБЛЕМА 4: Неэффективная сортировка при каждом вызове
        tasks.sort(Comparator.comparingInt(Task::getPriority).reversed());

        return tasks.remove(0);
    }

    // ПРОБЛЕМА 5: synchronized + тяжёлые вычисления
    public synchronized String processTask(Task task)
    {
        task.setStatus(TaskStatus.PROCESSING);

        long startTime = System.nanoTime();

        // ПРОБЛЕМА 6: Неэффективное кэширование
        String cacheKey = task.getId();
        if (cache.containsKey(cacheKey))
        {
            task.setStatus(TaskStatus.COMPLETED);
            processedCount++;
            return cache.get(cacheKey);
        }

        // Симуляция сложных вычислений
        String result = performHeavyComputation(task.getData());

        cache.put(cacheKey, result);
        task.setStatus(TaskStatus.COMPLETED);
        task.setProcessingTime(System.nanoTime() - startTime);
        processedCount++;

        return result;
    }

    // ПРОБЛЕМА 7: CPU-интенсивная операция в критической секции
    private String performHeavyComputation(String data)
    {
        try
        {
            Thread.sleep(10); // Симуляция вычислений

            // Симуляция обработки данных
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < 1000; i++)
            {
                result.append(data.hashCode() + i);
            }

            return result.toString();
        }
        catch (InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public synchronized int getProcessedCount()
    {
        return processedCount;
    }

    public synchronized int getPendingCount()
    {
        return tasks.size();
    }

    // ПРОБЛЕМА 8: Синхронизация на чтении статистики
    public synchronized Map<TaskStatus, Long> getStatistics()
    {
        Map<TaskStatus, Long> stats = new HashMap<>();

        // Считаем статистику каждый раз заново
        for (TaskStatus status : TaskStatus.values())
        {
            long count = tasks.stream()
                .filter(t -> t.getStatus() == status)
                .count();
            stats.put(status, count);
        }

        return stats;
    }
}
