package kz.kaznu.course.lab10.task3;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SynchronizedMapCounter
{
    private Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());

    public void count(String word)
    {
        synchronized (map)
        {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }
    }
}
