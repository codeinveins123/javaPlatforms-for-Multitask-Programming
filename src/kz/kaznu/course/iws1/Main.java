package kz.kaznu.course.iws1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main
{
    public static void main(String args[])
    {
        Map<String, Double>productRevenues = new HashMap<>();
        Map<String, Double>productRevenues2 = new HashMap<>();
        productRevenues.putIfAbsent("laptop", 125.5);
        productRevenues2.putIfAbsent("laptop", 201.0);
        productRevenues.putAll(productRevenues2);
        for(Map.Entry<String, Double> entry : productRevenues.entrySet())
        {
            System.out.println(entry.getKey() +  entry.getValue());
        }
    }
}
