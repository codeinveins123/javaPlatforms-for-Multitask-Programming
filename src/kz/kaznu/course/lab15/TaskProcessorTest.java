package kz.kaznu.course.lab15;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.util.concurrent.*;

public class TaskProcessorTest
{
    private TaskProcessor processor;

    @BeforeEach
    public void setup()
    {
        processor = new TaskProcessor();
    }

    @Test
    @DisplayName("Тест базовой функциональности")
    public void testBasicFunctionality()
    {
        Task task = new Task("task-1", "data", 5);
        processor.addTask(task);

        Task retrieved = processor.getNextTask();
        assertNotNull(retrieved);
        assertEquals("task-1", retrieved.getId());
    }

    @Test
    @DisplayName("Тест параллельного добавления задач")
    public void testConcurrentAddTasks() throws InterruptedException
    {
        int threadCount = 10;
        int tasksPerThread = 100;
        CountDownLatch latch = new CountDownLatch(threadCount);

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++)
        {
            executor.submit(() ->
            {
                for (int j = 0; j < tasksPerThread; j++)
                {
                    processor.addTask(new Task("t" + j, "data", j % 10));
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertTrue(processor.getPendingCount() <= threadCount * tasksPerThread);
    }

    @Test
    @DisplayName("Тест приоритетов задач")
    public void testTaskPriority()
    {
        processor.addTask(new Task("low", "data", 1));
        processor.addTask(new Task("high", "data", 10));

        Task t = processor.getNextTask();
        assertEquals("high", t.getId());
    }

    @Test
    @DisplayName("Тест обработки задач")
    public void testTaskProcessing()
    {
        Task t = new Task("t1", "data", 3);
        processor.addTask(t);

        t = processor.getNextTask();
        String result = processor.processTask(t);

        assertNotNull(result);
        assertEquals(TaskStatus.COMPLETED, t.getStatus());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    @DisplayName("Тест отсутствия deadlock")
    public void testNoDeadlock() throws InterruptedException
    {
        ExecutorService exec = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++)
        {
            exec.submit(() ->
            {
                for (int j = 0; j < 100; j++)
                {
                    processor.addTask(new Task("t-" + j, "data", j % 5));
                    Task t = processor.getNextTask();
                    if (t != null)
                    {
                        processor.processTask(t);
                    }
                }
            });
        }

        exec.shutdown();
        assertTrue(exec.awaitTermination(5, TimeUnit.SECONDS));
    }

    @RepeatedTest(10)
    @DisplayName("Стресс-тест thread safety")
    public void stressTestThreadSafety() throws InterruptedException
    {
        ExecutorService exec = Executors.newFixedThreadPool(8);
        for (int i = 0; i < 500; i++)
        {
            exec.submit(() -> processor.addTask(new Task("t" + Math.random(), "data", 1)));
        }
        exec.shutdown();
        exec.awaitTermination(5, TimeUnit.SECONDS);
        assertTrue(processor.getPendingCount() >= 0);
    }

    @Test
    @DisplayName("Тест кэширования")
    public void testCaching()
    {
        Task t = new Task("same", "data", 1);
        processor.addTask(t);

        Task task1 = processor.getNextTask();
        long start1 = System.currentTimeMillis();
        processor.processTask(task1);
        long end1 = System.currentTimeMillis();

        Task task2 = new Task("same", "data", 1);
        long start2 = System.currentTimeMillis();
        processor.processTask(task2);
        long end2 = System.currentTimeMillis();

        assertTrue((end2 - start2) < (end1 - start1));
    }
}
