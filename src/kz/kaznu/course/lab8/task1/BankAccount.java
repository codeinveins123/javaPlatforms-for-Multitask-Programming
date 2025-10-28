package kz.kaznu.course.lab8.task1;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
public class BankAccount
{
    private final ReentrantLock lock = new ReentrantLock();
    private final String accountNumber;
    private double balance;
    public BankAccount(String accountNumber, double initialBalance)
    {
        this.accountNumber = accountNumber;
        this.balance = initialBalance;
    }
    /**
     * Выполняет перевод денег на другой счет с защитой от deadlock
     *
     * @param to счет получателя
     * @param amount сумма перевода
     * @return true если перевод выполнен успешно, false если не удалось получить блокировки
     *
     * ЦЕЛЬ МЕТОДА: Безопасно перевести деньги между счетами, избегая deadlock
     * через использование tryLock с timeout
     */
    public boolean transfer(BankAccount to, double amount)
    {
        boolean fromLock = false, toLock = false;
        try
        {
            fromLock = this.lock.tryLock(0, TimeUnit.SECONDS);
            if(!fromLock)
            {
                return false;
            }

            toLock = to.lock.tryLock(0, TimeUnit.SECONDS);
            if(!toLock)
            {
                return false;
            }

            if(this.balance >= amount)
            {
                this.balance -= amount;
                to.balance += amount;
                System.out.println("Transfer successful");
                return true;
            }
            else
            {
                System.out.println("Transfer successless");
                return false;
            }
        }
        catch(InterruptedException e)
        {
            Thread.currentThread().interrupt();
            return false;
        }
        finally
        {
            if(toLock)   to.lock.unlock();
            if(fromLock) this.lock.unlock();
        }
    }

    public double getBalance()
    {
        lock.lock();
        try
        {
            return balance;
        }
        finally
        {
            lock.unlock();
        }
    }

    public void setBalance(double balance)
    {
        lock.lock();
        try
        {
            this.balance = balance;
        }
        finally
        {
            lock.unlock();
        }
    }

    public String getAccountNumber()
    {
        return accountNumber;
    }
    @Override
    public String toString()
    {
        return String.format("Account[%s]: %.2f ", accountNumber, getBalance());
    }
}

