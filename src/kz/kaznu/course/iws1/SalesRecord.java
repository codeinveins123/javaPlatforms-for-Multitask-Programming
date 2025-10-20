package kz.kaznu.course.iws1;

public class SalesRecord
{
    private int productId;
    private String productName;
    private int quantity;
    private double price;
    private String date;
    
    //Конструктор, принимающий все поля
    SalesRecord(int productId, String productName, int quantity, double price, String date)
    {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.date = date;
    }
    
    //Геттеры для всех полей
    int getId(){return productId;}
    String getName(){return productName;}
    int getQuantity(){return quantity;}
    double getPrice(){return price;}
    String getDate(){return date;}

    //Метод getTotalAmount(), возвращающий quantity * price
    double getTotalAmount(){return quantity * price;}
    
    @Override
    public String toString()
    {
        return String.format("%s: %d x %.2f тг = %.2f тг", 
                              productName, quantity, price, getTotalAmount());
    }
}
