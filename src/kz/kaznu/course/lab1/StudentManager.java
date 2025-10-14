package kz.kaznu.course.lab1;
import java.util.Scanner;

public class StudentManager
{
    public static void main(String[] args)
    {
        
        System.out.println("=== Система управления студентами ===\n");
        

        Student[] students = 
        {
            new Student("Айжан Сериков", 20, "ИТ-21-1", 3.8),
            new Student("Динара Абдуллаева", 19, "ИТ-21-2", 3.2),
            new Student("Арман Токтаров", 21, "ИТ-20-1", 2.9),
            new Student("Камила Нурланова", 20, "ИТ-21-1", 3.9)
        };

       
        System.out.println("Список студентов:\n");
        for (int i = 0; i < students.length; i++)
        {
            System.out.printf("%d. %s - %s\n",
                                i + 1, students[i], students[i].getStatus());
        }
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nВведите номер студента для подробной информации (1-" +
                            students.length + "): ");
        try 
        {
            int choice = scanner.nextInt();
            if (choice >= 1 && choice <= students.length)
            {
                Student selected = students[choice - 1];
                System.out.println("\n=== Подробная информация ===");
                System.out.println("Имя: " + selected.getName());
                System.out.println("Возраст: " + selected.getAge());
                System.out.println("Группа: " + selected.getGroup());
                System.out.println("GPA: " + selected.getGpa());
                System.out.println("Статус: " + selected.getStatus());
            } 
            else 
            {
                System.out.println("Неверный номер студента!");
            }
        } 
        catch (Exception e)
        {
            System.out.println("Ошибка ввода!");
        } 
            finally
        {
            scanner.close();
        }
            analyzeStudents(students);
        }

        private static void analyzeStudents(Student[] students)
        {
            System.out.println("\n=== Анализ успеваемости ===");
            double totalGpa = 0;
            Student bestStudent = students[0];
            int excellentCount = 0;
            for (Student student : students)
            {
                totalGpa += student.getGpa();
                if (student.getGpa() > bestStudent.getGpa())
                {
                    bestStudent = student;
                }
                if (student.getGpa() >= 3.5)
                {
                    excellentCount++;
                }
            }
            double averageGpa = totalGpa / students.length;
            System.out.printf("Средний GPA: %.2f\n", averageGpa);
            System.out.println("Лучший студент: " + bestStudent.getName() +
                                " (GPA: " + bestStudent.getGpa() + ")");
            System.out.println("Количество отличников: " + excellentCount +
                                " из " + students.length);
        }
}