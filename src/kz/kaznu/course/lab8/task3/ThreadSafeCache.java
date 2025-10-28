package kz.kaznu.course.lab8.task3;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.Lock;

public class ThreadSafeCache<K, V>
{
    private final Map<K, V> cache = new HashMap<>();
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock; // Для операций чтения
    private final Lock writeLock; // Для операций записи

    public ThreadSafeCache()
    {
        this.readLock = rwLock.readLock();
        this.writeLock = rwLock.writeLock();
    }

    /**
     * Получает значение из кеша
     *
     * @param key ключ
     * @return значение или null если ключа нет
     *
     * ЦЕЛЬ МЕТОДА: Безопасно прочитать значение из кеша, позволяя множественным
     * потокам читать одновременно для максимальной производительности
     */

    public V get(K key)
    {
    // TODO: Реализуйте метод get
    // 1. Получите readLock (несколько потоков могут читать одновременно!)
    // 2. В try блоке получите и верните значение из cache
    // 3. В finally освободите readLock
        readLock.lock();
        try
        {
            return cache.get(key);
        }
        finally
        {
            readLock.unlock();
        }
    }


    /**
     * Добавляет или обновляет значение в кеше
     *
     * @param key ключ
     * @param value значение
     *
     * ЦЕЛЬ МЕТОДА: Безопасно записать значение в кеш, получив эксклюзивный доступ
     * чтобы никто другой не мог читать или писать во время модификации
     */
    public void put(K key, V value)
    {
    // TODO: Реализуйте метод put
    // 1. Получите writeLock (блокирует ВСЕ другие операции!)
    // 2. В try блоке добавьте значение в cache
    // 3. В finally освободите writeLock
        writeLock.lock();
        try
        {
            cache.put(key, value);
        }
        finally
        {
            writeLock.unlock();
        }
        
    }


    /**
     * Удаляет значение из кеша
     *
     * @param key ключ
     * @return удаленное значение или null
     *
     * ЦЕЛЬ МЕТОДА: Безопасно удалить значение из кеша с эксклюзивным доступом
     */
    public V remove(K key)
    {
    // TODO: Реализуйте метод remove
    // 1. Получите writeLock
    // 2. В try блоке удалите и верните значение из cache
    // 3. В finally освободите writeLock
        writeLock.lock();
        try
        {
            V data = (V) cache.remove(key);
            return data;
        }
        finally
        {
            writeLock.unlock();
        }
        
    }


    /**
     * Получает значение или вычисляет его если отсутствует (double-checked locking pattern)
     *
     * @param key ключ
     * @param valueFactory функция для создания значения если его нет
     * @return существующее или новое значение
     *
     * ЦЕЛЬ МЕТОДА: Атомарно получить или создать значение, используя оптимизацию
     * double-checked locking для минимизации времени удержания write lock
     */
    public V computeIfAbsent(K key, java.util.function.Function<K, V> valueFactory)
    {
    // TODO: Реализуйте метод computeIfAbsent
    // Это сложный метод! Используем паттерн double-checked locking:
    //
    // 1. Сначала пробуем прочитать с readLock:
    // - Получите readLock
    // - Проверьте есть ли значение в cache
    // - Если есть - верните его
    // - Освободите readLock
    //
    // 2. Если значения нет, переходим к записи:
    // - Получите writeLock
    // - ВАЖНО: Проверьте снова (double-check)! Возможно другой поток уже добавил
    // - Если все еще нет - создайте значение через valueFactory.apply(key)
    // - Добавьте в cache
    // - Верните значение
    // - Освободите writeLock
        readLock.lock();
        try
        {
            V data = cache.get(key);
            if(data != null)
            {
                return data;
            }
        }
        finally
        {
            readLock.unlock();
        }

        writeLock.lock();
        try
        {
            V data = cache.get(key);
            if(data == null)
            {
                data = valueFactory.apply(key);
                cache.put(key, data);
            }
            return data;
        }
        finally
        {
            writeLock.unlock();
        }
    }


    /**
     * Возвращает текущий размер кеша
     */
    public int size()
    {
        readLock.lock();
        try
        {
            return cache.size();
        }
        finally
        {
            readLock.unlock();
        }
    }


    /**
     * Очищает весь кеш
     */
    public void clear()
    {
        writeLock.lock();
        try
        {
            cache.clear();
        }
        finally
        {
            writeLock.unlock();
        }
    }
}