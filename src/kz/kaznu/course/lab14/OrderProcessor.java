package kz.kaznu.course.lab14;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class OrderProcessor
{
    public static void main(String[] args)
    {
        OrderProcessingService service = new OrderProcessingService();

        // Создание тестовых заказов
        List<Order> orders = Arrays.asList(
            new Order("ORD001", "PROD001", 2, "customer1@example.com"),
            new Order("ORD002", "PROD002", 10, "customer2@example.com"),
            new Order("ORD003", "PROD003", 5, "customer3@example.com"),
            new Order("ORD004", "PROD004", 1, "customer4@example.com"),
            new Order("ORD005", "PROD005", 3, "customer5@example.com"),
            new Order("ORD006", "PROD999", 1, "customer6@example.com"), // Несуществующий товар
            new Order("ORD007", "PROD001", 100, "customer7@example.com") // Недостаточно товара
        );

        try
        {
            System.out.println("\n=== ПАРАЛЛЕЛЬНАЯ ОБРАБОТКА ===");
            long startParallel = System.currentTimeMillis();

            CompletableFuture<List<OrderResult>> resultsFuture = service.processMultipleOrders(orders);
            List<OrderResult> parallelResults = resultsFuture.join();
            long durationParallel = System.currentTimeMillis() - startParallel;

            printResults(parallelResults, durationParallel);

            System.out.println("\n=== ПОСЛЕДОВАТЕЛЬНАЯ ОБРАБОТКА ===");
            long startSequential = System.currentTimeMillis();

            List<OrderResult> sequentialResults = service.processOrdersSequentially(orders);
            long durationSequential = System.currentTimeMillis() - startSequential;

            printResults(sequentialResults, durationSequential);
        }
        finally
        {
            service.shutdown();
        }
    }

    private static void printResults(List<OrderResult> results, long duration)
    {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║ ИТОГОВЫЕ РЕЗУЛЬТАТЫ                          ║");
        System.out.println("╚══════════════════════════════════════════════╝\n");

        double totalAmount = 0;
        long successCount = 0;

        System.out.println("Успешные заказы:");
        for (OrderResult r : results)
        {
            if (r.isSuccess())
            {
                successCount++;
                totalAmount += r.getTotalAmount();
                System.out.printf("✓ %s - %.2f₸ - %s%n",
                    r.getOrderId(), r.getTotalAmount(), r.getMessage());
            }
        }

        System.out.println("\nНеуспешные заказы:");
        for (OrderResult r : results)
        {
            if (!r.isSuccess())
            {
                System.out.printf("✗ %s - %s%n",
                    r.getOrderId(), r.getMessage());
            }
        }

        System.out.println("════════════════════════════════════════════════");
        System.out.printf("Всего заказов: %d%n", results.size());
        System.out.printf("Успешных: %d (%.1f%%)%n", successCount,
            ((double) successCount / results.size()) * 100);
        System.out.printf("Неуспешных: %d (%.1f%%)%n", results.size() - successCount,
            ((double) (results.size() - successCount) / results.size()) * 100);
        System.out.printf("Общая сумма: %.2f₸%n", totalAmount);
        System.out.printf("Общее время обработки: %d мс%n", duration);
        System.out.println("════════════════════════════════════════════════");
    }
}
