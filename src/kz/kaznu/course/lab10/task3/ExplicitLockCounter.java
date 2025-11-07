package kz.kaznu.course.lab10.task3;

import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

public class ExplicitLockCounter
{
    private final Map<String, Integer> map = new HashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    
    public void count(String word)
    {
        lock.lock();
        try
        {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }
        finally
        {
            lock.unlock();
        }
    }
}
