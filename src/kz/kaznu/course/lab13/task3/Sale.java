package kz.kaznu.course.lab13.task3;

import java.time.LocalDate;

public class Sale
{ 
    private String product;
    private String category;
    private double price;
    private int quantity;
    private LocalDate date;

    public Sale(String product, String category, double price, int quantity, LocalDate date)
    {
        this.product = product;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.date = date;
    }
    
    public String getProduct()
    {
        return product;
    }

    public String getCategory()
    {
        return category;
    }

    public double getPrice()
    {
        return price;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public LocalDate getDate()
    {
        return date;
    }

    public double getTotalAmount()
    {
        return price * quantity;
    }

    @Override
    public String toString()
    {
        return String.format("%s (%s) - %.2f₸ x %d = %.2f₸",
                product, category, price, quantity, getTotalAmount());
    }
}
