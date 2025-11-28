package kz.kaznu.course.lab13.task2;

import kz.kaznu.course.lab12.task1.RandomArray;
import java.util.stream.Stream;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Arrays;

public class Main
{
    public static void main(String[] args)
    {
        List<Integer> numbers = Arrays.stream(RandomArray.createArray(200_000_000))
                                .boxed()
                                .collect(Collectors.toList());


        System.out.println("===== Сравнение призводительности потоков =====");
        System.out.println("Размер данных: " + numbers.size());
        System.out.println("Количество процессоров: " + Runtime.getRuntime().availableProcessors());


        System.out.println("----- Фильтрация четных чисел -----");

        long tStart1 = System.currentTimeMillis();
        List<Integer> evenNumbers = numbers.parallelStream()
                .filter(number -> number % 2 == 0)
                .collect(Collectors.toList());
        long tEnd1 = System.currentTimeMillis();

        System.out.println("Параллельная фильтрация: " + (float)(tEnd1 - tStart1) / 1000 + " cекунд");

        long tStart2 = System.currentTimeMillis();
        List<Integer> evenNumbersSeq = numbers.stream()
                .filter(number -> number % 2 == 0)
                .collect(Collectors.toList());  
        long tEnd2 = System.currentTimeMillis();

        System.out.println("\nПоследовательная фильтрация: " + (float)(tEnd2 - tStart2) / 1000 + " cекунд");

        System.out.printf("Ускорение: %.2fx\n", (float)(tEnd2 - tStart2) / (tEnd1 - tStart1));

        System.out.println("\n----- Арифметические операции -----");

        long tStart3 = System.currentTimeMillis();
        List<Integer> algebraNumbers = numbers.parallelStream()
                .map(number -> number + 5)
                .map(number -> number * number)
                .map(number -> number / 3)
                .map(number -> number * number * number)
                .map(number -> number - (int)(number * 0.5))
                .collect(Collectors.toList());
        long tEnd3 = System.currentTimeMillis();

        System.out.println("Параллельная арифметическая операция: " + (float)(tEnd3 - tStart3) / 1000 + " cекунд");

        long tStart4 = System.currentTimeMillis();
        List<Integer> algebraNumbersSeq = numbers.stream()
                .map(number -> number + 5)
                .map(number -> number * number)
                .map(number -> number / 3)
                .map(number -> number * number * number)
                .map(number -> number - (int)(number * 0.5))
                .collect(Collectors.toList());
        long tEnd4 = System.currentTimeMillis();

        System.out.println("\nПоследовательная арифметическая операция: " + (float)(tEnd4 - tStart4) / 1000 + " cекунд");

        System.out.printf("Ускорение: %.2fx\n", (float)(tEnd4 - tStart4) / (tEnd3 - tStart3));


    }
}   