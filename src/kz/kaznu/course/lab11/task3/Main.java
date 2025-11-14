package kz.kaznu.course.lab11.task3;

import java.util.concurrent.ThreadLocalRandom;
import java.util.Arrays;

public class Main {
    public static void main(String[] args)
    {
        //Решил сделать сортировку матрицы, чтобы было легко видно работу процессора
        int[][] matrix = randomMatrix();
        sortMatrix(matrix);

        System.out.println("Initial matrix:");
        printMatrix(matrix);

        int coresCount = (ThreadLocalRandom.current().nextInt(1, 5)) + 1;
        System.out.println("Cores count: " + coresCount);
        MatrixProccesor processor = new MatrixProccesor(matrix, coresCount);
        processor.startProcessing();
    }

    private static void printMatrix(int[][] matrix)
    {
        for (int[] row : matrix)
        {
            for (int value : row)
            {
                System.out.printf("[%3d]", value);
            }
            System.out.println();
        }
        System.out.println();
    }

    private static int[][] randomMatrix()
    {
        int rows = ThreadLocalRandom.current().nextInt(3, 15);
        int cols = ThreadLocalRandom.current().nextInt(2, 9);
        int[][] matrix = new int[rows][cols];
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                matrix[i][j] = ThreadLocalRandom.current().nextInt(1, 100);
            }
        }
        return matrix;
    }

    private static void sortMatrix(int[][] matrix)
    {
        int rows = matrix.length;
        if (rows == 0)
        {
            return;
        }
        int cols = matrix[0].length;

        int[] all = new int[rows * cols];
        int k = 0;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                all[k++] = matrix[i][j];
            }
        }

        Arrays.sort(all);

        k = 0;
        for (int i = 0; i < rows; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                matrix[i][j] = all[k++];
            }
        }
    }
}
