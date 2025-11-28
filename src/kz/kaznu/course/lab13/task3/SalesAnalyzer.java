package kz.kaznu.course.lab13.task3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SalesAnalyzer
{
    private static double getTotalAmount(Sale record)
    {
        return record.getTotalAmount();
    }

    private static String getName(Sale record)
    {
        return record.getProduct();
    }

    private static int getQuantity(Sale record)
    {
        return record.getQuantity();
    }

    private static double getPrice(Sale record)
    {
        return record.getPrice();
    }

    private static List<Sale> readSalesFromCsv(String filePath)
    {
        try (Stream<String> lines = Files.lines(Paths.get(filePath)))
        {
            return lines.skip(1)
                        .map(line ->
                        {
                            try
                            {
                                String[] parts = line.split(",");
                                String productName = parts[1].trim();
                                int quantity = Integer.parseInt(parts[2].trim());
                                double price = Double.parseDouble(parts[3].trim());
                                LocalDate date = LocalDate.parse(parts[4].trim());

                                return new Sale(productName, "Без категории", price, quantity, date);
                            }
                            catch (Exception e)
                            {
                                System.err.println("Ошибка при обработке строки: " + line + ", " + e.getMessage());
                                return null;
                            }
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
        }
        catch (IOException e)
        {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static void main(String[] args)
    {
        String csvFilePath = "branches/sales_branch1_big.csv";
        List<Sale> sales = readSalesFromCsv(csvFilePath);

        analyzeSales(sales);
    }

    private static void analyzeSales(List<Sale> sales)
    {
        if (sales.isEmpty())
        {
            System.out.println("Нет данных для анализа");
            return;
        }

        showTotalRevenue(sales);
        showTopSellingProduct(sales);
        showTop5ExpensiveSales(sales);
        showSalesStatistics(sales);
    }

    private static void showTotalRevenue(List<Sale> sales)
    {
        double totalRevenue = sales.stream()
                                   .mapToDouble(SalesAnalyzer::getTotalAmount)
                                   .sum();

        System.out.printf("Общая сумма продаж: %.2f$%n%n", totalRevenue);
    }

    private static void showTopSellingProduct(List<Sale> sales)
    {
        Map<String, Integer> productQuantities = sales.stream()
                                                      .collect(Collectors.groupingBy(
                                                          Sale::getProduct,
                                                          Collectors.summingInt(Sale::getQuantity)
                                                      ));

        String topProduct = productQuantities.entrySet().stream()
                                             .max(Map.Entry.comparingByValue())
                                             .map(entry -> String.format("%s - %d шт.", entry.getKey(), entry.getValue()))
                                             .orElse("Не найдено");

        System.out.println("Самый продаваемый товар: " + topProduct);
    }

    private static void showTop5ExpensiveSales(List<Sale> sales)
    {
        List<Map.Entry<String, Double>> top5Sales = sales.stream()
                                                         .map(record -> new AbstractMap.SimpleEntry<>(getName(record), getTotalAmount(record)))
                                                         .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                                                         .limit(5)
                                                         .collect(Collectors.toList());

        System.out.println("\nТоп-5 самых дорогих продаж:");
        top5Sales.forEach(entry ->
        {
            System.out.printf("%s - %.2f$%n", entry.getKey(), entry.getValue());
        });
    }

    private static void showSalesStatistics(List<Sale> sales)
    {
        DoubleSummaryStatistics priceStats = sales.stream()
                                                  .mapToDouble(SalesAnalyzer::getPrice)
                                                  .summaryStatistics();

        System.out.println("\nОбщая статистика по продажам:");
        System.out.printf("Всего продаж: %d%n", sales.size());
        System.out.printf("Минимальная цена: %.2f$%n", priceStats.getMin());
        System.out.printf("Максимальная цена: %.2f$%n", priceStats.getMax());
        System.out.printf("Средняя цена: %.2f$%n", priceStats.getAverage());

        int totalQuantity = sales.stream()
                                 .mapToInt(SalesAnalyzer::getQuantity)
                                 .sum();
        System.out.printf("Общее количество проданных товаров: %d%n", totalQuantity);
    }
}
