package kz.kaznu.course.lab8.task2;

public class BoundedQueueTest
{
 public static void main(String[] args) throws InterruptedException
 {
    BoundedQueue<String> queue = new BoundedQueue<>(5);
    // Производитель
    Thread producer = new Thread(() ->
    {
    try
    {
        for (int i = 1; i <= 10; i++)
        {
            String item = "Item-" + i;
            System.out.println("Producer: добавляю " + item);
            queue.put(item);
            System.out.println("Producer: добавил " + item + ", размер очереди: " + queue.size());
            Thread.sleep(100);
        }
            System.out.println("Producer: завершил работу");
    } 
    catch (InterruptedException e)
    {
        Thread.currentThread().interrupt();
    }
    });

    // Потребитель 1
    Thread consumer1 = new Thread(() ->
    {
    try
    {
        for (int i = 0; i < 5; i++)
        {
        String item = queue.take();
        System.out.println("Consumer-1: взял " + item);
        Thread.sleep(200);
        }
        System.out.println("Consumer-1: завершил работу");
    }
    catch (InterruptedException e)
    {
        Thread.currentThread().interrupt();
    }
    });

    // Потребитель 2
    Thread consumer2 = new Thread(() ->
    {
    try
    {
        Thread.sleep(500); // Начинаем позже
        for (int i = 0; i < 5; i++)
        {
            String item = queue.take();
            System.out.println("Consumer-2: взял " + item);
            Thread.sleep(150);
        }
        System.out.println("Consumer-2: завершил работу");
    } 
    catch (InterruptedException e)
    {  
        Thread.currentThread().interrupt();
    }
    });

    producer.start();
    consumer1.start();
    consumer2.start();

    producer.join();

    consumer1.join();
    consumer2.join();
    System.out.println("\nОчередь пустая: " + queue.isEmpty());
    }
}

