package kz.kaznu.course.lab15;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
public class SynchronizationBenchmark
{
    private TaskProcessor synchronizedProcessor;
    private TaskProcessorOptimized optimizedProcessor;
    private static final int OPERATIONS = 100;

    @Setup
    public void setup()
    {
        synchronizedProcessor = new TaskProcessor();
        optimizedProcessor = new TaskProcessorOptimized();

        for (int i = 0; i < OPERATIONS; i++)
        {
            synchronizedProcessor.addTask(new Task("task-" + i, "data-" + i, i % 10));
            optimizedProcessor.addTask(new Task("task-" + i, "data-" + i, i % 10));
        }
    }

    // TODO: Бенчмарк добавления задач (проблемная)
    @Benchmark
    public void testSynchronizedAddTask()
    {
        synchronizedProcessor.addTask(new Task("x", "data", 5));
    }

    // TODO: Бенчмарк добавления задач (оптимизированная)
    @Benchmark
    public void testOptimizedAddTask()
    {
        optimizedProcessor.addTask(new Task("y", "data", 5));
    }

    // TODO: Бенчмарк обработки задач (проблемная)
    @Benchmark
    public void testSynchronizedProcessTask()
    {
        Task t = synchronizedProcessor.getNextTask();
        if (t != null)
        {
            synchronizedProcessor.processTask(t);
        }
    }

    // TODO: Бенчмарк обработки задач (оптимизированная)
    @Benchmark
    public void testOptimizedProcessTask()
    {
        Task t = optimizedProcessor.getNextTask();
        if (t != null)
        {
            optimizedProcessor.processTask(t);
        }
    }

    public static void main(String[] args) throws Exception
    {
        org.openjdk.jmh.Main.main(args);
    }
}
