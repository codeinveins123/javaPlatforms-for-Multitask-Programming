package kz.kaznu.course.lab1;


public class Student
{
    private String  name;
    private int     age;
    private String  group;
    private double  gpa;

    //Конструктор
    public Student(String name, int age, String group, double gpa)
    {
        this.name   = name;
        this.age    = age;
        this.group  = group;
        this.gpa    = gpa;
    }

    //Геттеры
    public String getName()     { return name; }
    public int getAge()         { return age; }
    public String getGroup()    { return group; }
    public double getGpa()      { return gpa; }

    //Как я понял переопределяем функцию из Java под свои нужны, типа void info(){}
    @Override
    public String toString()
    {
        return String.format("Student{name='%s', age=%d, group='%s', gpa=%.2f}",
                            name, age, group, gpa);
    }

    public String getStatus()
    {
        if (gpa >= 3.5) return "Отличник";
        else if (gpa >= 3.0) return "Хорошист";
        else if (gpa >= 2.0) return "Удовлетворительно";
        else return "Неудовлетворительно";
    }
}