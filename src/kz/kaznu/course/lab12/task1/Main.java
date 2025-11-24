package kz.kaznu.course.lab12.task1;

public class Main
{
    public static void main(String[] args)
    {
        int[] array = RandomArray.createArray(1_000_000_000);

        long tStart1 = System.currentTimeMillis();

        ArraySumTask arraySumTask = new ArraySumTask(array, 0, array.length);

        System.out.println("==== Parallel sum array ====");
        System.out.println("Size array: " + array.length);
        System.out.println("Threshold: " + arraySumTask.getThreshold());

        System.out.println("Result: " + arraySumTask.invoke());
        long tEnd1 = System.currentTimeMillis();
        System.out.println("Parralel time: " + (tEnd1 - tStart1));

        long tStart2 = System.currentTimeMillis();
        System.out.println("Result: " + sequenceArraySum(array));
        long tEnd2 = System.currentTimeMillis();
        System.out.println("Sequense time: " + (tEnd2 - tStart2));

        System.out.printf("Speedup: %.2f\n", (float)(tEnd2 - tStart2) / (tEnd1 - tStart1));
        
    }

    static Long sequenceArraySum(int[] array)
    {
        System.out.println("===== Sequence sum array =====");
        long sum = 0;
        for(int i = 0; i < array.length; ++i)
        {
            sum += array[i];
        }
        return sum;
    }
}
