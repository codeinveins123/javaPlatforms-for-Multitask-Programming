package kz.kaznu.course.lab10.task2;

import java.util.concurrent.CopyOnWriteArrayList;
 
public class NotificationSystem
{
    private CopyOnWriteArrayList<Subscriber> subscribers = new CopyOnWriteArrayList<>();
    
    public void subscribe(Subscriber listener)
    {
        subscribers.add(listener);
    }

    public void unsubscribe(Subscriber listener)
    {
        subscribers.remove(listener);
    }

    public void notifyAll(String message)
    {
        for (Subscriber listener : subscribers)
        {
            listener.onNotify(message);
        }
    }

    public Integer getSubscribersCount()
    {
        return subscribers.size();
    }

    public Subscriber getSubscriber(int number)
    {
        if(number < subscribers.size())
        {
            return subscribers.get(number);
        }
        return null;
    }
}
