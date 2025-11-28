package kz.kaznu.course.lab13.task1;

import java.util.Random;
import java.util.stream.Stream;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Map;

public class Main
{
    public static void main(String[] args)
    {
        List<Student> students = randomStudents();
        
        System.out.println("===== Обработка данных студентов =====");

        System.out.println("1. Студенты с GPA > 3.5");
                        
        List<Student> studentsGpa = students.stream()
            .filter(student -> student.getGpa() > 3.5)
            .limit(10)
            .collect(Collectors.toList());
        
        printStudents(studentsGpa);

        System.out.println("\n2. Студенты отсортированные по имени");

        List<Student> studentsSortedByName = students.stream()
                .sorted((s1, s2) -> s1.getName().compareTo(s2.getName()))
                .limit(10)
                .collect(Collectors.toList());

        printStudents(studentsSortedByName);

        System.out.println("\n3. Имена студентов 3-ого курса");

        List<Student> studentsThirdCourse = students.stream()
                .filter(student -> student.getCourse() == 3)
                .limit(10)
                .collect(Collectors.toList());

        printStudents(studentsThirdCourse);

        System.out.println("\n4. Средний GPA всех студентов");

        double averageGpa = students.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);

        System.out.println("Средний GPA: " + averageGpa);

        System.out.println("\n5. Студент с максимальным GPA");

        Student studentMaxGPA = students.stream()
            .max((s1, s2) -> Double.compare(s1.getGpa(), s2.getGpa()))
            .orElse(null);

        System.out.println(studentMaxGPA);

        System.out.println("\n6. Студенты по курсам");

        Map<Integer, List<Student>> byCourse = students.stream()
                .collect(Collectors.groupingBy(Student::getCourse));

        byCourse.forEach((course, studentsList) -> System.out.println("Курс " + course + ": " + studentsList.size() + " студентов"));

        System.out.println("\n7. Количество студентов на каждом курсе");

        Map<Integer, Long> countByCourse = students.stream()
                .collect(Collectors.groupingBy(Student::getCourse, Collectors.counting()));

        countByCourse.forEach((course, count) -> System.out.println("Курс " + course + ": " + count + " студентов"));

    }

    public static List<Student> randomStudents()
    {
        String[] names = {"Abraam", "Daniil", "Daria", "Dmitry", "Michael", "Olga", "Sergey", "Tatyana", "Vladimir", "Yuri", "Zinaida"};
        Random random = new Random();
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < (random.nextInt(25) + 5); i++)
        {
            students.add(new Student(names[random.nextInt(names.length)],
                    random.nextInt(7) + 18,
                    random.nextInt(5) + 1, 
                    random.nextDouble() * 4));
        }
        return students;
    }

    public static void printStudents(List<Student> students)
    {
        for (Student student : students)
        {
            System.out.println(student);
        }
    }
}
