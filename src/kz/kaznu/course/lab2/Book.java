package kz.kaznu.course.lab2;

import java.time.Year;

public class Book extends Item
{
    private String genre;
    private int pageCount;

    private void valid(String genre, int pageCount)
    {
        if(genre == null || genre.isEmpty()) throw new IllegalArgumentException("genre empty");
        if(pageCount <= 0 || pageCount > 500) throw new IllegalArgumentException("Incorrect pageCount");
    }

    public Book(String title, String author, Year year, String genre, int pageCount, boolean isAvailable)
    {
        super(title, author, year, isAvailable);
        valid(genre, pageCount);
        this.genre = genre;
        this.pageCount = pageCount;
    }

    public String getGenre() {return genre;}
    public int getPageCount(){return pageCount;}

    public void setGenre(String genre)
    {
        valid(genre, pageCount);
        this.genre = genre;
    }

    public void setPageCount(int pageCount)
    {
        valid(genre, pageCount);
        this.pageCount = pageCount;
    }

    @Override
    public void borrowItem()
    {
        if(isAvailable){this.isAvailable = false;}
        else throw new IllegalArgumentException("Book is not available");
        System.out.println("Book has borrow");
    }

    @Override
    public void returnItem()
    {
        if(!isAvailable){this.isAvailable = true;}
        else throw new IllegalArgumentException("Book is available");
        System.out.println("Book has return");
    }

    @Override
    public String toString()
    {
        String extra = String.format("Genre: %-20s Pages: %-3d", genre, pageCount);
        return String.format("| %-9s | %-30s | %-30s | %4s | %-43s | %-11s |",
                            "Book", getTitle(), getAuthor(), getYear(), extra,
                            (isAvailable ? "Available" : "Borrowed"));
    }




}
