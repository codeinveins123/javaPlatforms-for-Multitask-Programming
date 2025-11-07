package kz.kaznu.course.lab10.task1;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class WebCache
{
    private ConcurrentHashMap<String, String> cache;
    private ConcurrentHashMap<String, Integer> accessCount;

    WebCache()
    {
        cache = new ConcurrentHashMap<>();
        accessCount = new ConcurrentHashMap<>();
    }

    public String get(String url)
    {
        String data = cache.get(url);
        accessCount.merge(url, 1, Integer::sum);
        return data;
    }

    public void put(String url, String data)
    {
        cache.put(url, data);
        accessCount.putIfAbsent(url, 0);
    }

    public int getAccessCount(String url)
    {
        return accessCount.getOrDefault(url, 0);
    }

    public Map<String, Integer> getTopAccessed(int n)
    {
        List<Map.Entry<String, Integer>> list = new ArrayList<>(accessCount.entrySet());
        list.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));
        return list.stream()
            .limit(n)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (a, b) -> a,
                java.util.LinkedHashMap::new
            ));
    }

}