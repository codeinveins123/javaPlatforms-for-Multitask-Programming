package kz.kaznu.course.lab2;

import java.time.Year;



public class Magazine extends Item
{
    private static int ISSUE_NUMBER = 10000000;
    private int issueNumber;
    private int frequency; //per day

    private void valid(int issueNumber, int frequency)
    {
        if(issueNumber < 10000000 || issueNumber > 99999999) throw new IllegalArgumentException("Illegal issue number");
        if(frequency < 0 || frequency > 365) throw new IllegalArgumentException("Illegal frequency!");
    }

    public Magazine(String title, String author, Year year, int frequency, boolean isAvailable)
    {
        super(title, author, year, isAvailable);
        this.issueNumber = ISSUE_NUMBER++;
        this.frequency = frequency;
    }

    public Magazine(String title, String author, Year year, int issueNumber, int frequency, boolean isAvailable)
    {
        super(title, author, year, isAvailable);
        valid(issueNumber, frequency);
        this.issueNumber = issueNumber;
        this.frequency = frequency;
    }

    public int getIssueNumber(){return issueNumber;}
    public int getFrequensy(){return frequency;}

    public void setFrequency(int frequency)
    {
        valid(issueNumber, frequency);
        this.frequency = frequency;
    }

    public void setIssueNumber(int issueNumber)
    {
        valid(issueNumber, frequency);
        this.issueNumber = issueNumber;
    }
    
    @Override
    public void borrowItem()
    {
        if(isAvailable){this.isAvailable = false;}
        else throw new IllegalArgumentException("Magazine is not available");
        System.out.println("Magazine has borrow");
    }

    @Override
    public void returnItem()
    {
        if(!isAvailable){this.isAvailable = true;}
        else throw new IllegalArgumentException("Magazine is available");
        System.out.println("Magazine has return");
    }

    @Override
    public String toString()
    {
        String extra = String.format("Issue: %-20d Freq: %-6d", issueNumber, frequency);
        return String.format("| %-9s | %-30s | %-30s | %4s | %-43s | %-11s |",
                            "Magazine", getTitle(), getAuthor(), getYear(), extra,
                            (isAvailable ? "Available" : "Borrowed"));
    }



}

