package kz.kaznu.course.lab2;
import java.time.Year;

public class Item 
{
    private String title;
    private String author;
    private Year year;
    protected boolean isAvailable;

    private void valid(String title, String author, Year year)
    {
        if(title == null || title.isEmpty()) throw new IllegalArgumentException("Empty title");
        if(author == null || author.isEmpty()) throw new IllegalArgumentException("Empty author");
        if(year == null) throw new IllegalArgumentException("year cannot be null type");

        int Y = year.getValue();
        if( Y < 1600 || Y > Year.now().getValue()) throw new IllegalArgumentException("Incorrect date");
    }

    public Item(String title, String author, Year year, boolean isAvailable)
    {
        valid(title, author, year);
        this.title = title;
        this.author = author;
        this.year = year;
        this.isAvailable = isAvailable;
    }

    public String getTitle()      {return title;}
    public String getAuthor()     {return author;}
    public String getYear()       {return year.toString();}
    public Year getYearObject()   {return year;}
    public Boolean isAvailable()  {return isAvailable;}

    public void setTitle(String title)
    {
        valid(title, author, year);
        this.title = title;
    }

    public void setAuthor(String author)
    {
        valid(title, author, year);
        this.author = author;
    }

    public void setYear(Year year)
    {
        valid(title, author, year);
        this.year = year;
    }

    public void borrowItem()
    {
        if(isAvailable) this.isAvailable = false;
        else  throw new IllegalArgumentException("Is not available!");
        System.out.println("Item has borrow");
    }

    public void returnItem()
    {
        if(!isAvailable) this.isAvailable = true;
        else throw new IllegalArgumentException("Is available!");
        System.out.println("Item has return");
    }

}
