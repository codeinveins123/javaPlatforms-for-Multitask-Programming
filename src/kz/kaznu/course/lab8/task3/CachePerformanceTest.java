package kz.kaznu.course.lab8.task3;

public class CachePerformanceTest {
    public static void main(String[] args) throws InterruptedException
    {
        ThreadSafeCache<String, Integer> cache = new ThreadSafeCache<>();
        // Заполняем кеш начальными данными
        for (int i = 0; i < 100; i++)
        {
            cache.put("key-" + i, i);
        }
        System.out.println("Начальный размер кеша: " + cache.size());

        // Создаем много читателей (имитируем высокую нагрузку на чтение)
        Thread[] readers = new Thread[10];
        for (int i = 0; i < readers.length; i++)
        {
            final int threadId = i;
            readers[i] = new Thread(() ->
            {
                for (int j = 0; j < 1000; j++)
                {
                    String key = "key-" + (j % 100);
                    Integer value = cache.get(key);
                    if (j % 200 == 0)
                    {
                        System.out.println("Reader-" + threadId + " прочитал: " + key + " = " + value);
                    }
                }
            }, "Reader-" + i);
        }

        // Создаем несколько писателей (редкие записи)
        Thread[] writers = new Thread[2];
        for (int i = 0; i < writers.length; i++)
        {
            final int threadId = i;
            writers[i] = new Thread(() ->
            {
                for (int j = 0; j < 50; j++)
                {
                    String key = "key-" + (j % 100);
                    cache.put(key, j * 1000);
                    System.out.println(" Writer-" + threadId + " записал: " + key + " = " + (j * 1000));
                    try
                    {
                        Thread.sleep(20);
                    } 
                    catch (InterruptedException e) {}
                }
            }, "Writer-" + i);
        }
        long startTime = System.currentTimeMillis();

        // Запускаем все потоки
        for (Thread reader : readers)
        {
            reader.start();
        }
        for (Thread writer : writers) 
        {
            writer.start();
        }

        // Ждем завершения
        for(Thread reader : readers)
        {
            reader.join();
        }

        for(Thread writer : writers)
        {
            writer.join();
        }

        long endTime = System.currentTimeMillis();
        System.out.println("\nКонечный размер кеша: " + cache.size());
        System.out.println("Время выполнения: " + (endTime - startTime) + " мс");

        // Тест computeIfAbsent
        System.out.println("\nТест computeIfAbsent:");
        Integer computed = cache.computeIfAbsent("new-key", key ->
        {
            System.out.println(" Вычисляем значение для " + key);
            return 9999;
        });
        System.out.println("Результат: " + computed);

        // Повторный вызов не должен вычислять значение
        Integer cached = cache.computeIfAbsent("new-key", key ->
        {
            System.out.println(" Это не должно выполниться!");
            return 8888;
        });
        System.out.println("Закешированное значение: " + cached);
    }
}