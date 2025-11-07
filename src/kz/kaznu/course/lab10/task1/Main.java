package kz.kaznu.course.lab10.task1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main
{
    public static void main(String[] args)
    {
        WebCache webCache = new WebCache();

        ExecutorService executor = Executors.newFixedThreadPool(5);

        for(int i = 0; i < 100; ++i)
        {
            int urlID = i;
            executor.submit(() -> {
                String url = "https://example.com/page/" + (urlID % 20);
                String data = "Data for page " + urlID;
                webCache.put(url, data);
            });
        }

        for(int i = 0; i < 1000; ++i)
        {
            executor.submit(() -> {
                int rand = java.util.concurrent.ThreadLocalRandom.current().nextInt(20);
                String url = "https://example.com/page/" + rand;
                String data = webCache.get(url);
            });
        }

        executor.shutdown();
        try
        {
            executor.awaitTermination(1, TimeUnit.MINUTES);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        
        System.out.println("ТОП 5 самых популярных страниц:");
        webCache.getTopAccessed(5).forEach((url, count) ->
        System.out.println(url + ": " + count + " обращений"));
    }
}
