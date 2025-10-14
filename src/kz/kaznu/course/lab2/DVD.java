package kz.kaznu.course.lab2;

import java.time.Year;

public class DVD extends Item
{
    private int duration;       //minutes
    private String director;

    private void valid(int duration, String director)
    {
        if(duration <= 0 || duration >= 240) throw new IllegalArgumentException("Invalid duration");
        if(director == null || director.isEmpty()) throw new IllegalArgumentException("Director invalid");
    }

    public DVD(String title, String author, Year year, int duration, String director, boolean isAvailable)
    {
        super(title, author, year, isAvailable);
        valid(duration, director);
        this.duration = duration;
        this.director = director;
    }

    public int getDuration(){return duration;}
    public String getDirector(){return director;}

    public void setDuration(int duration)
    {
        valid(duration, director);
        this.duration = duration;
    }

    public void setDirector(String director)
    {
        valid(duration, director);
        this.director = director;
    }

    @Override
    public void borrowItem()
    {
        if(isAvailable){this.isAvailable = false;}
        else throw new IllegalArgumentException("DVD is not available");
        System.out.println("DVD has borrow");
    }

    @Override
    public void returnItem()
    {
        if(!isAvailable){this.isAvailable = true;}
        else throw new IllegalArgumentException("DVD is available");
        System.out.println("DVD has return");
    }

    @Override
    public String toString()
    {
        String extra = String.format("%-20s, Duration: %3d min", director, duration);
        return String.format("| %-9s | %-30s | %-30s | %4s | %-43s | %-11s |",
                            "DVD", getTitle(), getAuthor(), getYear(), extra,
                            (isAvailable ? "Available" : "Borrowed"));
    }




}
