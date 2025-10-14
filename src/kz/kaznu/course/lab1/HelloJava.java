package kz.kaznu.course.lab1;
/**
* Первая программа на Java
* Демонстрирует базовые возможности языка
*
* @author vasilev daniil xd
* @version 1.0
*/
public class HelloJava
{

    /**
    * Точка входа в программу
    * @param args аргументы командной строки
    */
    public static void main(String[] args)
    {
        System.out.println("=== Добро пожаловать в мир Java! ===");

        // Информация о системе
        System.out.println("Java версия: " + System.getProperty("java.version"));
        System.out.println("Операционная система: " + System.getProperty("os.name"));
        System.out.println("Пользователь: " + System.getProperty("user.name"));

        // Работа с аргументами
        if (args.length > 0)
        {
            System.out.println("\nПереданные аргументы:");
            for (int i = 0; i < args.length; i++)
            {
                System.out.println(" args[" + i + "] = " + args[i]);
            }
        } 
            else 
        {
            System.out.println("\nАргументы командной строки не переданы");
        }
            System.out.println("\n=== Программа завершена успешно ===");
    }

}