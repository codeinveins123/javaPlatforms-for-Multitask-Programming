package kz.kaznu.course.lab13.task1;

public class Student
{
    private String name;
    private int age;
    private int course;
    private double gpa;

    public Student(String name, int age, int course, double gpa)
    {
        this.name = name;
        this.age = age;
        this.course = course;
        this.gpa = gpa;
    }

    public String getName()
    {
        return name;
    }

    public int getAge()
    {
        return age;
    }

    public int getCourse()
    {
        return course;
    }

    public double getGpa()
    {
        return gpa;
    }

    @Override
    public String toString()
    {
        return String.format("%-10s | age:%2d | course:%2d | gpa:%.2f", name, age, course, gpa);
    }
}
