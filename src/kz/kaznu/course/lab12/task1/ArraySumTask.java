package kz.kaznu.course.lab12.task1;

import java.util.concurrent.RecursiveTask;

public class ArraySumTask extends RecursiveTask<Long>
{
    private final int[] array;
    private final int start;
    private final int end;
    private final int THRESHOLD = 10_000_000;
    
    ArraySumTask(int[] array, int start, int end)
    {
        this.array = array;
        this.start = start;
        this.end = end;
    }

    @Override
    protected Long compute() 
    {
        int length = end - start;
        if(length < THRESHOLD)
        {
            long sum = 0;
            for(int i = start; i < end; i++)
            {
                sum += array[i];
            }
            return sum;
        }
        
        int mid = (start + end) / 2;
        ArraySumTask leftTask = new ArraySumTask(array, start, mid);
        ArraySumTask rightTask = new ArraySumTask(array, mid, end);

        leftTask.fork();
        long rightResult = rightTask.compute();
        long leftResult = leftTask.join();
        
        return leftResult + rightResult;
    }

    public int getThreshold()
    {
        return THRESHOLD;
    }
}
