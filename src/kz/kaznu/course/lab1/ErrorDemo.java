package kz.kaznu.course.lab1;

public class ErrorDemo
{
    public static void main(String[] args)
    {
        System.out.println("=== Демонстрация ошибок ===");
        // 1. Исправьте синтаксическую ошибку:
        System.out.println("Привет мир"); // отсутствует ;

        // 2. Исправьте ошибку времени выполнения:
        int[] array = {1, 2, 3};
        System.out.println(array[2]);

        // 3. Исправьте логическую ошибку:
        for (int i = 0; i < 10; ++i)
        {
            System.out.println("Элемент " + i);
        }

        // 4. Работа с null
        String text = "";
        System.out.println(text.length());
        System.out.println("Все ошибки исправлены!");
    }
}