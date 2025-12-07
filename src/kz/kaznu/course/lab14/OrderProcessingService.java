package kz.kaznu.course.lab14;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class OrderProcessingService
{
    private ExecutorService executor;
    private Map<String, Product> inventory;
    private Random random;

    public OrderProcessingService()
    {
        this.executor = Executors.newFixedThreadPool(10);
        this.inventory = initializeInventory();
        this.random = new Random();
    }

    private Map<String, Product> initializeInventory()
    {
        Map<String, Product> inv = new HashMap<>();
        inv.put("PROD001", new Product("PROD001", "Ноутбук", 150000.0, 10));
        inv.put("PROD002", new Product("PROD002", "Компьютер", 100000.0, 15));
        inv.put("PROD003", new Product("PROD003", "Телефон", 50000.0, 20));
        inv.put("PROD004", new Product("PROD004", "Планшет", 75000.0, 25));
        inv.put("PROD005", new Product("PROD005", "Телевизор", 200000.0, 30));
        return inv;
    }

    // --- Параллельная обработка ---
    public CompletableFuture<OrderResult> processOrder(Order order)
    {
        System.out.printf("%n=== Начало обработки заказа %s ===%n", order.getOrderId());
        long startTime = System.currentTimeMillis();

        return checkProductAvailability(order)
            .thenCompose(product ->
            {
                CompletableFuture<Double> priceFuture = calculateTotalPrice(order, product);
                CompletableFuture<Boolean> paymentFuture = priceFuture.thenCompose(price -> processPayment(order, price));
                CompletableFuture<Void> reserveFuture = paymentFuture.thenCompose(paymentSuccess -> reserveProduct(order, product));
                return priceFuture.thenCombine(reserveFuture, (price, v) -> price);
            })
            .thenCompose(finalPrice ->
                sendNotification(order, true, finalPrice)
                    .thenApply(v -> new OrderResult(
                        order.getOrderId(),
                        true,
                        "Заказ успешно обработан",
                        finalPrice
                    ))
            )
            .orTimeout(10, TimeUnit.SECONDS)
            .handle((result, ex) ->
            {
                if (ex != null)
                {
                    return new OrderResult(
                        order.getOrderId(),
                        false,
                        "Ошибка обработки заказа: " + ex.getMessage(),
                        0.0
                    );
                }
                return result;
            })
            .whenComplete((result, ex) ->
            {
                long duration = System.currentTimeMillis() - startTime;

                if (ex == null && result.isSuccess())
                {
                    System.out.printf("[%s] ✓ Заказ успешно обработан за %d мс%n",
                        order.getOrderId(), duration);
                }
                else
                {
                    System.out.printf("[%s] ✗ Ошибка обработки: %s%n",
                        order.getOrderId(),
                        result != null ? result.getMessage() : "Неизвестная ошибка");
                }
            });
    }

    // --- Последовательная обработка ---
    public List<OrderResult> processOrdersSequentially(List<Order> orders)
    {
        List<OrderResult> results = new ArrayList<>();

        for (Order order : orders)
        {
            try
            {
                Product product = inventory.get(order.getProductId());

                if (product == null)
                {
                    throw new RuntimeException("Товар не найден");
                }

                if (product.getStockQuantity() < order.getQuantity())
                {
                    throw new RuntimeException("Недостаточно товара на складе");
                }

                double price = product.getPrice() * order.getQuantity();

                if (order.getQuantity() >= 5)
                {
                    price -= price * 0.1;
                }

                price += price * 0.12;

                boolean payment = Math.random() < 0.9;
                if (!payment)
                {
                    throw new RuntimeException("Ошибка платежа");
                }

                product.setStockQuantity(product.getStockQuantity() - order.getQuantity());

                System.out.printf("[%s] Уведомление отправлено на %s%n",
                    order.getOrderId(), order.getCustomerEmail());

                results.add(new OrderResult(order.getOrderId(), true, "Заказ успешно обработан", price));
            }
            catch (Exception e)
            {
                results.add(new OrderResult(order.getOrderId(), false, e.getMessage(), 0.0));
            }
        }

        return results;
    }

    public CompletableFuture<List<OrderResult>> processMultipleOrders(List<Order> orders)
    {
        System.out.println("\n╔══════════════════════════════════════════════╗");
        System.out.println("║ ПАРАЛЛЕЛЬНАЯ ОБРАБОТКА ЗАКАЗОВ               ║");
        System.out.println("╚══════════════════════════════════════════════╝");

        long startTime = System.currentTimeMillis();

        List<CompletableFuture<OrderResult>> futures = orders.stream()
            .map(this::processOrder)
            .collect(Collectors.toList());

        CompletableFuture<Void> allOrdersFuture = CompletableFuture.allOf(
            futures.toArray(new CompletableFuture[0])
        );

        return allOrdersFuture.thenApply(v ->
        {
            List<OrderResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

            long successfulOrders = results.stream()
                .filter(OrderResult::isSuccess)
                .count();

            long failedOrders = results.size() - successfulOrders;
            long duration = System.currentTimeMillis() - startTime;

            System.out.printf("\n=== СТАТИСТИКА ОБРАБОТКИ ===%n");
            System.out.printf("Всего заказов: %d%n", results.size());
            System.out.printf("Успешных: %d%n", successfulOrders);
            System.out.printf("Неудачных: %d%n", failedOrders);
            System.out.printf("Общее время: %d мс%n", duration);
            System.out.printf("Среднее время на заказ: %.2f мс%n", (double) duration / results.size());
            System.out.println("================================\n");

            return results;
        });
    }

    public CompletableFuture<Product> checkProductAvailability(Order order)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                System.out.printf("[%s] Проверка наличия товара...%n", order.getOrderId());
                Thread.sleep(500);

                Product product = inventory.get(order.getProductId());
                if (product == null)
                {
                    throw new RuntimeException("Товар не найден");
                }

                if (product.getStockQuantity() < order.getQuantity())
                {
                    throw new RuntimeException("Недостаточное количество товара");
                }

                return product;
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Прервано");
            }
        }, executor);
    }

    public CompletableFuture<Double> calculateTotalPrice(Order order, Product product)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                System.out.printf("[%s] Расчёт стоимости...%n", order.getOrderId());
                Thread.sleep(500);

                int quantity = order.getQuantity();
                double discount = quantity >= 5 ? 0.1 : 0;
                double tax = 0.12;
                double price = product.getPrice() * quantity;
                price -= price * discount;
                price += price * tax;

                return price;
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Прервано");
            }
        }, executor);
    }

    public CompletableFuture<Boolean> processPayment(Order order, double amount)
    {
        return CompletableFuture.supplyAsync(() ->
        {
            try
            {
                System.out.printf("[%s] Обработка платежа на сумму %.2f₸...%n",
                    order.getOrderId(), amount);
                Thread.sleep(500);

                double randomValue = random.nextDouble();
                return randomValue < 0.9;
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Прервано");
            }
        }, executor);
    }

    public CompletableFuture<Void> reserveProduct(Order order, Product product)
    {
        return CompletableFuture.runAsync(() ->
        {
            try
            {
                System.out.printf("[%s] Резервирование товара %s...%n",
                    order.getOrderId(), product.getName());
                Thread.sleep(500);

                Product inventoryProduct = inventory.get(product.getProductId());
                int currentQuantity = inventoryProduct.getStockQuantity();
                int reservedQuantity = order.getQuantity();
                inventoryProduct.setStockQuantity(currentQuantity - reservedQuantity);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Прервано");
            }
        }, executor);
    }

    public CompletableFuture<Void> sendNotification(Order order, boolean success, double amount)
    {
        return CompletableFuture.runAsync(() ->
        {
            try
            {
                System.out.printf("[%s] Отправка уведомления на %s...%n",
                    order.getOrderId(), order.getCustomerEmail());
                Thread.sleep(500);

                if (success)
                {
                    System.out.printf("[%s] Заказ успешно оформлен на сумму %.2f₸%n",
                        order.getOrderId(), amount);
                }
                else
                {
                    System.out.printf("[%s] Заказ отменён, сумма %.2f₸%n",
                        order.getOrderId(), amount);
                }
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Прервано");
            }
        }, executor);
    }

    public void shutdown()
    {
        executor.shutdown();

        try
        {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS))
            {
                executor.shutdownNow();
            }
        }
        catch (InterruptedException e)
        {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
