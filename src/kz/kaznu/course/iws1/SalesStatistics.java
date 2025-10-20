package kz.kaznu.course.iws1;

import java.util.*;

public class SalesStatistics
{
    private int totalRecords;
    private double totalRevenue;
    private Map<String, Integer> productQuantities;
    private Map<String, Double> productRevenues;
    
    public SalesStatistics()
    {
        this.totalRecords = 0;
        this.totalRevenue = 0.0;
        this.productQuantities = new HashMap<>();
        this.productRevenues = new HashMap<>();
    }
    
    // TODO: Создайте метод addRecord(SalesRecord record)
    // Этот метод должен:
    // 1. Увеличить totalRecords на 1
    // 2. Добавить к totalRevenue сумму record.getTotalAmount()
    // 3. Обновить productQuantities (добавить количество к существующему или создать новую запись)
    // 4. Обновить productRevenues (добавить выручку к существующей или создать новую запись)
    public void addRecord(SalesRecord record)
    {
        totalRecords++;
        totalRevenue += record.getTotalAmount();
        Integer quantity = productQuantities.get(record.getName());
        if(quantity == null)
        {
            productQuantities.putIfAbsent(record.getName(), record.getQuantity());
        }
        else
        {
            quantity += record.getQuantity();
            productQuantities.put(record.getName(),  quantity);
        }

        Double revenue = productRevenues.get(record.getName());
        if(revenue == null)
        {
            productRevenues.putIfAbsent(record.getName(), record.getTotalAmount());
        }
        else
        {
            revenue += record.getTotalAmount();
            productRevenues.put(record.getName(),  revenue);
        }
    }
    
    // TODO: Создайте метод merge(SalesStatistics other)
    // Этот метод должен объединить текущую статистику с другой
    // 1. Сложить totalRecords
    // 2. Сложить totalRevenue
    // 3. Объединить productQuantities (суммировать значения для одинаковых ключей)
    // 4. Объединить productRevenues (суммировать значения для одинаковых ключей)
    void merge(SalesStatistics other)
    {
        Map<String, Integer> otherProductQuantities = other.getProductQuantities();
        Map<String, Double> otherProductRevenues = other.getProductRevenues();
        totalRecords += other.getTotalRecords();
        totalRevenue += other.getTotalRevenue();
        
        for(String productName : otherProductQuantities.keySet())
        {
            boolean hasProduct = productQuantities.containsKey(productName);
            Integer quantity = otherProductQuantities.get(productName);
            if(hasProduct)
            {
                quantity += productQuantities.get(productName);
                productQuantities.put(productName, quantity);
            }
            else
            {
                productQuantities.putIfAbsent(productName, quantity);
            }
        }

        for(String productName : otherProductRevenues.keySet())
        {
            boolean hasProduct = productRevenues.containsKey(productName);
            Double revenue = otherProductRevenues.get(productName);
            if(hasProduct)
            {
                revenue += productRevenues.get(productName);
                productRevenues.put(productName, revenue);
            }
            else
            {
                productRevenues.putIfAbsent(productName, revenue);
            }
        }
    }


    public void printReport()
    {
        System.out.println("\n=== ОТЧЕТ ПО ПРОДАЖАМ ===");
        System.out.println("Всего записей обработано: " + totalRecords);
        System.out.printf("Общая выручка: %.2f тг\n", totalRevenue);
        
        System.out.println("\n--- Топ 5 товаров по количеству ---");
        // TODO: Выведите топ 5 товаров по количеству проданных единиц
        // Используйте productQuantities
        ArrayList<Map.Entry<String, Integer>>topQuantities = new ArrayList<>();
        for(Map.Entry<String, Integer> entry : productQuantities.entrySet())
        {
            topQuantities.add(entry);
        }
        topQuantities.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
        for(int i = 0; i < topQuantities.size() && i <= 4; ++i)
        {
            System.out.printf("%d. %-10s: %-5d\n", i + 1, topQuantities.get(i).getKey(), topQuantities.get(i).getValue());
        }
        
        System.out.println("\n--- Топ 5 товаров по выручке ---");
        // TODO: Выведите топ 5 товаров по выручке
        // Используйте productRevenues
        ArrayList<Map.Entry<String, Double>>topRevenues = new ArrayList<>();
        for(Map.Entry<String, Double> entry : productRevenues.entrySet())
        {
            topRevenues.add(entry);
        }
        topRevenues.sort(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()));
        for(int i = 0; i < topRevenues.size() && i <= 4; ++i)
        {
            System.out.printf("%d. %-10s: %-5f\n", i + 1, topRevenues.get(i).getKey(), topRevenues.get(i).getValue());
        }
    }
    
    public int getTotalRecords() { return totalRecords; }
    public double getTotalRevenue() { return totalRevenue; }
    //добавил
    public Map<String, Integer> getProductQuantities() { return productQuantities;}
    public Map<String, Double> getProductRevenues() { return productRevenues;}

}

