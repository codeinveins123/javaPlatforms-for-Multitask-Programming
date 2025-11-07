package kz.kaznu.course.lab10.task2;

public class Main
{
    public static void main(String[] args) throws InterruptedException
    {
        NotificationSystem notificationSystem = new NotificationSystem();

        // Поток 1: Добавляет подписчиков
        Thread subscriberThread = new Thread(() ->
        {
            for (int i = 0; i < 100; i++)
            {
                notificationSystem.subscribe(new EmailSubscriber("user" + i + "@example.com"));
                try
                {
                    Thread.sleep(5);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
                
            }
        });

        // Поток 2: Рассылает уведомления
        Thread notifierThread = new Thread(() ->
        {
        for (int i = 0; i < 50; i++)
        {
            notificationSystem.notifyAll("Уведомление #" + i);
                try
                {
                    Thread.sleep(2);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }
        }
        });

        // Поток 3: Удаляет некоторых подписчиков
        Thread unsubscriberThread = new Thread(() ->
        {
            try
            {
                Thread.sleep(500);
            }
            catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
            }
            // Удаляем каждого 3-го подписчика
            for(int i = 0; i < notificationSystem.getSubscribersCount(); i++)
            {
                if(i % 3 == 0)
                {
                    notificationSystem.unsubscribe(notificationSystem.getSubscriber(i));
                }
            }
        });

        subscriberThread.start();
        notifierThread.start();
        unsubscriberThread.start();

        subscriberThread.join();
        notifierThread.join();
        unsubscriberThread.join();

        System.out.println("Итого подписчиков: " + notificationSystem.getSubscribersCount());

    }
}
