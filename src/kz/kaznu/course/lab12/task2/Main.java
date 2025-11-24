package kz.kaznu.course.lab12.task2;

import java.io.File;
import java.util.ArrayList;

public class Main
{
    public static void main(String[] args)
    {
        File file = new File("Folder");
        FileSearchTask fileSearchTask = new FileSearchTask(file, ".txt", new ArrayList<>());
        long timeStart = System.currentTimeMillis();
        fileSearchTask.invoke();
        long timeEnd = System.currentTimeMillis();

        System.out.println("\u001B[1m \u001B[32m ======== Parallel parsing files ========");
        System.out.println("Root directory: ThisProject/Folder");
        System.out.println("Find extension: .txt");
        System.out.println("Files found: ");
        fileSearchTask.printResults();

        System.out.println("Time: " + (timeEnd - timeStart) + " ms");
        System.out.println("Files found: " + fileSearchTask.getCounter());
    }
}
