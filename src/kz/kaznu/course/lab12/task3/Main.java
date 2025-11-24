package kz.kaznu.course.lab12.task3;

import kz.kaznu.course.lab12.task1.RandomArray;

class Main
{
    public static void main(String[] args)
    {
        int[] ParralelArray = RandomArray.createArray(300_000_000);
        int[] SequentialArray = ParralelArray.clone();
        printBigArray(ParralelArray);

        long tStart1 = System.currentTimeMillis();

        MergeSortTask mergeSortTask = new MergeSortTask(ParralelArray);

        mergeSortTask.invoke();
        long tEnd1 = System.currentTimeMillis();

        System.out.println("==== Parallel merge sort array ====");
        System.out.println("Size array: " + ParralelArray.length);
        System.out.println("Threshold: " + mergeSortTask.getThreshold());
        System.out.println("Result: ");
        printBigArray(ParralelArray);
        System.out.println("Parralel time: " + (tEnd1 - tStart1));

        long tStart2 = System.currentTimeMillis();
        MergeSortTask.mergeSort(SequentialArray, 0, SequentialArray.length - 1);
        long tEnd2 = System.currentTimeMillis();

        System.out.println("==== Sequential merge sort array ====");
        System.out.println("Result: ");
        printBigArray(SequentialArray);
        System.out.println("Sequense time: " + (tEnd2 - tStart2));

        System.out.printf("Speedup: %.2f\n", (float)(tEnd2 - tStart2) / (tEnd1 - tStart1));
    }

    public static void printBigArray(int[] array)
    {
        if(array.length >= 10)
        {
                System.out.println("First 5 elements array: ");
                
                for(int i = 0; i < 5; ++i)
                {
                    System.out.printf("%d ", array[i]);
            }

            System.out.println("\nLast 5 elements array:");

            for(int i = array.length - 5; i < array.length; ++i)
            {
                System.out.printf("%d ", array[i]);
            }
            System.out.println();
        }
        else
        {
            System.out.println("The array is not big enough");
        }
    }
}
