package kz.kaznu.course.lab10.task2;

public class EmailSubscriber implements Subscriber
{
    private String email;

    public EmailSubscriber(String email)
    {
        this.email = email;
    }

    @Override
    public void onNotify(String message)
    {
        // Симулируем отправку email (задержка)

                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                }

        System.out.println("Email sent to " + email + ": " + message);
    }
}
