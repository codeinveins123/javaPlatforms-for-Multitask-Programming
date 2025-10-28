package kz.kaznu.course.iws1;

import java.util.*;

public class ParallelSalesAnalysis
{
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== СИСТЕМА ПАРАЛЛЕЛЬНОГО АНАЛИЗА ПРОДАЖ ===");
        System.out.println("Доступно процессоров: " + Runtime.getRuntime().availableProcessors());
        
        // TODO: Создайте массив с именами файлов для обработки
        // Например: "sales_branch1.csv", "sales_branch2.csv", "sales_branch3.csv"
/*          "sales_branch1.csv",
            "sales_branch2.csv",
            "sales_branch3.csv",
            "sales_branch1_big.csv",
            "sales_branch2_big.csv",
            "sales_branch3_big.csv" */
        String[] filenames =
        {
            "branches/sales_mixed_branch1.csv",
            "branches/sales_mixed_branch2.csv",
            "branches/sales_mixed_branch3.csv",
            "branches/sales_mixed_branch2.csv",
            "branches/sales_mixed_branch4.csv",
            "branches/sales_mixed_branch5.csv",
            "branches/sales_mixed_branch6.csv",
            "branches/sales_mixed_branch7.csv",
            "branches/sales_mixed_branch8.csv",
            "branches/sales_mixed_branch9.csv",
            "branches/sales_mixed_branch10.csv",
            "branches/sales_mixed_branch11.csv",
            "branches/sales_mixed_branch12.csv",
            "branches/sales_mixed_branch13.csv",
            "branches/sales_mixed_branch14.csv",
            "branches/sales_mixed_branch15.csv",
            "branches/sales_mixed_branch16.csv",
            "branches/sales_mixed_branch17.csv",
            "branches/sales_mixed_branch18.csv",
            "branches/sales_mixed_branch19.csv",
            "branches/sales_mixed_branch20.csv",
        };
        
        // TODO: Запустите параллельную обработку
        long parallelTime = processParallel(filenames);
        
        System.out.println("\n=== РЕЗУЛЬТАТЫ ===");
        System.out.println("Параллельная обработка заняла: " + parallelTime + " мс");
        
        // БОНУС: Раскомментируйте для сравнения с последовательной обработкой
        long sequentialTime = processSequential(filenames);
        System.out.println("Последовательная обработка заняла: " + sequentialTime + " мс");
        double speedup = (double) sequentialTime / parallelTime;
        System.out.printf("Ускорение: %.2fx\n", speedup);

        long sequentialTimeWithoutThread = processSequentialWithoutThread(filenames);
        System.out.println("Последовательная обработка без потока заняла: "  + sequentialTimeWithoutThread + " мс" );
        double speedupWithoutThread = (double) sequentialTime / sequentialTimeWithoutThread;
        System.out.printf("Ускорение: %.2fx\n", speedupWithoutThread);
    }
    
    public static long processParallel(String[] filenames) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        
        // TODO: Реализуйте параллельную обработку
        // 1. Создайте список FileProcessor для каждого файла
        // 2. Создайте список Thread для каждого процессора
        // 3. Запустите все потоки
        // 4. Дождитесь завершения всех потоков с помощью join()
        // 5. Проверьте, не было ли ошибок (errorMessage != null)
        // 6. Соберите статистику из всех процессоров
        // 7. Выведите итоговый отчет
        
        List<FileProcessor> processors = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();

        for(var file : filenames)
        {
            processors.add(new FileProcessor(file));
        }
        for(var processor : processors)
        {
            threads.add(new Thread(processor));
        }
        
        for(var thread : threads)
        {
            thread.start();
        }

        for(var thread : threads)
        {
            try
            {
                thread.join();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }

        for(var processor : processors)
        {
            if(processor.getErrorMessage() != null)
            {
                System.out.print(processor.getFilename() + " " + processor.getErrorMessage());
            }
        }

        SalesStatistics statistics = new SalesStatistics();
        for(var processor : processors)
        {
            if(processor.isCompleted() && processor.getErrorMessage() == null )
            {
                for(var record : processor.getResults())
                {
                    statistics.addRecord(record);
                }               
            }
        }
        statistics.printReport();

        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
    
    public static long processSequential(String[] filenames)
    {
        long startTime = System.currentTimeMillis();
        
        List<FileProcessor> processors = new ArrayList<>();
        for(var file : filenames)
        {
            processors.add(new FileProcessor(file));
        }
        for(var processor : processors)
        {
            processor.run();
        }

        for(var processor : processors)
        {
            if(processor.getErrorMessage() != null)
            {
                System.out.print(processor.getFilename() + " " + processor.getErrorMessage());
            }
        }

        SalesStatistics statistics = new SalesStatistics();
        for(var processor : processors)
        {
            if(processor.isCompleted() && processor.getErrorMessage() == null)
            {
                for(var record : processor.getResults())
                {
                    statistics.addRecord(record);
                }               
            }
        }
        statistics.printReport();
        
        
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    public static long processSequentialWithoutThread(String[] filenames)
    {
        long startTime = System.currentTimeMillis();

        List<FileProcessor> processors = new ArrayList<>();
        for(var file : filenames)
        {
            processors.add(new FileProcessor(file));
        }
        for(var processor : processors)
        {
            processor.run();
        }

        for(var processor : processors)
        {
            if(processor.getErrorMessage() != null)
            {
                System.out.print(processor.getFilename() + " " + processor.getErrorMessage());
            }
        }

        SalesStatistics statistics = new SalesStatistics();
        for(var processor : processors)
        {
            if(processor.isCompleted() && processor.getErrorMessage() == null)
            {
                for(var record : processor.getResults())
                {
                    statistics.addRecord(record);
                }               
            }
        }
        statistics.printReport();
        
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}
