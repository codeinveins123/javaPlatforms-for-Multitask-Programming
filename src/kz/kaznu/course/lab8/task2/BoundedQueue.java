package kz.kaznu.course.lab8.task2;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
public class BoundedQueue<T>
{
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull;    // Условие: очередь не полная
    private final Condition notEmpty;   // Условие: очередь не пустая

    private final Object[] items;   // Буфер для хранения элементов
    private int putIndex = 0;       // Индекс для добавления
    private int takeIndex = 0;      // Индекс для извлечения
    private int count = 0;          // Текущее количество элементов в очереди

    public BoundedQueue(int capacity)
    {
        this.items = new Object[capacity];
        this.notFull = lock.newCondition();
        this.notEmpty = lock.newCondition();
    }

    /**
     * Добавляет элемент в очередь. Если очередь полная, ждет освобождения места.
     *
     * @param item элемент для добавления
     * @throws InterruptedException если поток прерван во время ожидания
     *
     * ЦЕЛЬ МЕТОДА: Безопасно добавить элемент в очередь, ожидая если она полная,
     * и уведомить потребителей что появился новый элемент
     */
    public void put(T item) throws InterruptedException
    {
        lock.lock();
        try
        {
            while(size() == items.length)
            {
                notFull.await();
            }
            items[putIndex] = item;
            putIndex = (putIndex + 1) % items.length;
            count++;

            notEmpty.signal();
        }
        finally
        {
            lock.unlock();
        }

    // TODO: Реализуйте метод put
    // 1. Получите блокировку (lock.lock())
    // 2. В блоке try:
    // - Пока очередь полная (count == items.length), ждите на условии notFull
    // - Добавьте элемент в items[putIndex]
    // - Обновите putIndex циклически: putIndex = (putIndex + 1) % items.length
    // - Увеличьте count
    // - Уведомите одного ждущего потребителя через notEmpty.signal()
    // 3. В блоке finally освободите блокировку
    }

    /**
     * Извлекает элемент из очереди. Если очередь пустая, ждет появления элемента.
     *
     * @return извлеченный элемент
     * @throws InterruptedException если поток прерван во время ожидания
     *
     * ЦЕЛЬ МЕТОДА: Безопасно извлечь элемент из очереди, ожидая если она пустая,
     * и уведомить производителей что освободилось место
     */
    public T take() throws InterruptedException
    {
        lock.lock();
        try
        {
            while(count == 0)
            {
                notEmpty.await();
            }

            T item = (T) items[takeIndex];

            items[takeIndex] = null;
            takeIndex = (takeIndex + 1) % items.length;
            count--;
            notFull.signal();

            return item;
        }
        finally
        {
            lock.unlock();
        }

    // TODO: Реализуйте метод take
    // 1. Получите блокировку (lock.lock())
    // 2. В блоке try:
    // - Пока очередь пустая (count == 0), ждите на условии notEmpty
    // - Извлеките элемент из items[takeIndex]
    // - Обнулите items[takeIndex] = null (для GC)
    // - Обновите takeIndex циклически: takeIndex = (takeIndex + 1) % items.length
    // - Уменьшите count
    // - Уведомите одного ждущего производителя через notFull.signal()
    // - Верните извлеченный элемент
    // 3. В блоке finally освободите блокировку

    }
    /**
     * Возвращает текущее количество элементов в очереди
     */
    public int size()
    {
        lock.lock();
        try
        {
            return count;
        }
        finally
        {
            lock.unlock();
        }
    }

    public boolean isEmpty()
    {
        lock.lock();
        try
        {
            return count == 0;
        }
        finally
        {
            lock.unlock();
        }
    }
}
