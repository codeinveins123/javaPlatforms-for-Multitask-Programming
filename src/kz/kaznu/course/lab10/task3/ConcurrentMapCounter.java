package kz.kaznu.course.lab10.task3;

import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentMapCounter
{
    private ConcurrentHashMap<String, Integer> hashCounter;

    ConcurrentMapCounter()
    {
        hashCounter = new ConcurrentHashMap<>();
    }

    public void count(String word)
    {
        hashCounter.merge(word, 1, Integer::sum);
    }

}
