package kz.kaznu.course.lab12.task3;

import java.util.concurrent.RecursiveAction;

public class MergeSortTask extends RecursiveAction
{
    private final int[] array;
    private final int[] tmp;
    private final int start;
    private final int end;
    private final int threshold = 5_000_000;

    public MergeSortTask(int[] array)
    {
        this(array, new int[array.length], 0, array.length - 1);
    }

    private MergeSortTask(int[] array, int[] tmp, int start, int end)
    {
        this.array = array;
        this.tmp = tmp;
        this.start = start;
        this.end = end;
    }

    @Override
    protected void compute()
    {
        //Сделал свою сортировку чтобы не сравнивать с не понятной сортировкой из библиотек
        //Буду сравнивать с этой же через статик метод
        if (end - start <= threshold)
        {
            mergeSortRecursive(array, tmp, start, end);
            return;
        }

        int mid = start + (end - start) / 2;
        MergeSortTask left = new MergeSortTask(array, tmp, start, mid);
        MergeSortTask right = new MergeSortTask(array, tmp, mid + 1, end);

        left.fork();
        right.compute();
        left.join();
        merge(array, tmp, start, mid, end);
    }

    public static void mergeSort(int[] array, int start, int end)
    {
        if (start >= end)
        {
            return;
        }

        int[] tmp = new int[array.length];
        mergeSortRecursive(array, tmp, start, end);
    }

    private static void mergeSortRecursive(int[] arr, int[] tmp, int start, int end)
    {
        if (start >= end)
        {
            return;
        }

        int mid = start + (end - start) / 2;
        mergeSortRecursive(arr, tmp, start, mid);
        mergeSortRecursive(arr, tmp, mid + 1, end);
        merge(arr, tmp, start, mid, end);
    }

    private static void merge(int[] arr, int[] tmp, int start, int mid, int end)
    {
        System.arraycopy(arr, start, tmp, start, end - start + 1);

        int i = start;
        int j = mid + 1;

        for (int k = start; k <= end; k++)
        {
            if (i > mid)
            {
                arr[k] = tmp[j++];
            }
            else if (j > end)
            {
                arr[k] = tmp[i++];
            }
            else if (tmp[j] < tmp[i])
            {
                arr[k] = tmp[j++];
            }
            else
            {
                arr[k] = tmp[i++];
            }
        }
    }

    public int getThreshold()
    {
        return threshold;
    }
}
