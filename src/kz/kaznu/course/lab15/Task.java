package kz.kaznu.course.lab15;

public class Task
{
    private final String id;
    private final String data;
    private final int priority;
    private TaskStatus status;
    private long processingTime;

    public Task(String id, String data, int priority)
    {
        this.id = id;
        this.data = data;
        this.priority = priority;
        this.status = TaskStatus.PENDING;
        this.processingTime = 0;
    }

    // Геттеры и сеттеры
    public String getId()
    {
        return id;
    }

    public String getData()
    {
        return data;
    }

    public int getPriority()
    {
        return priority;
    }

    public TaskStatus getStatus()
    {
        return status;
    }

    public void setStatus(TaskStatus status)
    {
        this.status = status;
    }

    public long getProcessingTime()
    {
        return processingTime;
    }

    public void setProcessingTime(long time)
    {
        this.processingTime = time;
    }

    @Override
    public String toString()
    {
        return String.format("Task{id='%s', priority=%d, status=%s}", id, priority, status);
    }
}

enum TaskStatus
{
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED
}
