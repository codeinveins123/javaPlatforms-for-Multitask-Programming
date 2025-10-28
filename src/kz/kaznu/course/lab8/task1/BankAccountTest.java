package kz.kaznu.course.lab8.task1;

public class BankAccountTest
{
    public static void main(String[] args) throws InterruptedException
    {
        BankAccount account1 = new BankAccount("number1", 1000.0);
        BankAccount account2 = new BankAccount("number2", 1000.0);
        BankAccount account3 = new BankAccount("number3", 1000.0);

        System.out.println("Изначальное состояние:");
        System.out.println(account1);
        System.out.println(account2);
        System.out.println(account3);

        Thread t1 = new Thread(() ->
        {
            for (int i = 0; i < 5; i++)
            {
                account1.transfer(account2, 50);
                try
                {
                    Thread.sleep(10);
                } 
                catch (InterruptedException e){}
            }
        }, "Thread-1");

        Thread t2 = new Thread(() ->
        {
            for (int i = 0; i < 5; i++)
            {
                account2.transfer(account1, 30);
                try
                { 
                    Thread.sleep(10);
                }
                catch (InterruptedException e) {}
            }
        }, "Thread-2");

        Thread t3 = new Thread(() ->
        {
            for (int i = 0; i < 5; i++)
            {
                account1.transfer(account3, 20);
                try
                {
                    Thread.sleep(10);
                } 
                catch (InterruptedException e) {}
            }
        }, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        t1.join();
        t2.join();
        t3.join();

        System.out.println("\nКонечное состояние:");
        System.out.println(account1);
        System.out.println(account2);
        System.out.println(account3);


        double total = account1.getBalance() + account2.getBalance() + account3.getBalance();
        System.out.println("\nОбщая сумма: " + total + " (должно быть 3000.0)");
        
    }
}

