package kz.kaznu.course.lab12.task1;

public class RandomArray
{
    private final int[] array;


    //Сделал чисто для чисел, чисто чтобы решить задачки, а не для реализации полноценного случайного шаблонного массива
    //Почему сделал шаблонные, а не сразу int, потому что захотел потестиь
    public RandomArray(int size)
    {
        this.array = new int[size];
        for (int i = 0; i < size; i++)
        {
            array[i] = (int)(Math.random() * 100) + (int)(Math.random() * (int)(Math.random() * 100));
        }
    }

    public int[] getArray()
    {
        return array;
    }
    
    public static int[] createArray(int size)
    {
        return new RandomArray(size).getArray();
    }
}