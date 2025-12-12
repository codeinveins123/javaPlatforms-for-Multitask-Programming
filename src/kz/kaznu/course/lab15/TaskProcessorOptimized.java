package kz.kaznu.course.lab15;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class TaskProcessorOptimized
{
    private final PriorityBlockingQueue<Task> tasks;
    private final ConcurrentHashMap<String, String> cache;
    private final AtomicInteger processedCount;

    private volatile Map<TaskStatus, Long> cachedStatistics;
    private volatile long lastStatsUpdate;
    private static final long STATS_CACHE_DURATION = 1000; // 1 секунда

    public TaskProcessorOptimized()
    {
        this.tasks = new PriorityBlockingQueue<>(
            100,
            Comparator.comparingInt(Task::getPriority).reversed()
        );
        this.cache = new ConcurrentHashMap<>();
        this.processedCount = new AtomicInteger(0);
    }

    public void addTask(Task task)
    {
        tasks.offer(task); // Lock-free добавление
    }

    public Task getNextTask()
    {
        return tasks.poll(); // Lock-free извлечение
    }

    public String processTask(Task task)
    {
        task.setStatus(TaskStatus.PROCESSING);
        long startTime = System.nanoTime();

        // Оптимизация: computeIfAbsent, тяжёлое вычисление вне synchronized
        String result = cache.computeIfAbsent(task.getId(), key ->
        {
            return performHeavyComputation(task.getData());
        });

        task.setStatus(TaskStatus.COMPLETED);
        task.setProcessingTime(System.nanoTime() - startTime);
        processedCount.incrementAndGet();

        return result;
    }

    private String performHeavyComputation(String data)
    {
        try
        {
            Thread.sleep(50);
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

    public int getProcessedCount()
    {
        return processedCount.get();
    }

    public int getPendingCount()
    {
        return tasks.size();
    }

    public Map<TaskStatus, Long> getStatistics()
    {
        long now = System.currentTimeMillis();
        if (cachedStatistics == null || now - lastStatsUpdate > STATS_CACHE_DURATION)
        {
            Map<TaskStatus, Long> stats = new HashMap<>();
            for (TaskStatus status : TaskStatus.values())
            {
                long count = tasks.stream().filter(t -> t.getStatus() == status).count();
                stats.put(status, count);
            }
            cachedStatistics = stats;
            lastStatsUpdate = now;
        }
        return cachedStatistics;
    }
}
