package kz.kaznu.course.lab11.task3;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class MatrixProccesor 
{
    private final int[][] matrix;
    private final Thread[] cores;
    private final int coresCount;

    private int stage = 0;

    public MatrixProccesor(int[][] matrix, int coresCount) 
    {
        this.matrix = matrix;
        this.coresCount = coresCount;
        this.cores = new Thread[coresCount];
    }

    public void startProcessing() 
    {
        CyclicBarrier barrier = new CyclicBarrier(coresCount, () -> 
        {
            //Эта функция будет выполнятся после каждого await()
            //synchronized, чтобы не было каши в выводе
            synchronized (System.out)
            {
                System.out.println(); 
                switch (stage)
                {
                    case 1 -> System.out.println("Stage 1 - mul elements on 2:");
                    case 2 -> System.out.println("Stage 2 - add 10 to each element:");
                    case 3 -> System.out.println("Stage 3 - div elements on 3:");
                }
                printMatrix(matrix);
                System.out.println();
            }
        });

        int rows = matrix.length;
        int baseRows = rows / coresCount;
        int extra = rows % coresCount;
        int start = 0;

        //Тут, если у нас остались лишние строки, а их будет не больше чем кол-во потоков
        //(так как rows % coresCount всегда меньше чем кол-во потоков)
        //И мы их распределяем по потокам по 1 на каждый.
        //Можно было отдать лишиние просто последнему потоку, но это плохое решение
        for (int i = 0; i < coresCount; i++)
        {
            int end = start + baseRows;
            if (extra > 0)
            {
                end++;
                extra--;
            }
            //Если пишет x to x, то это значит, что поток простаивает.
            System.out.println("Core " + i + " processing rows " + start + " to " + end);
            cores[i] = new Thread(new Core(start, end, barrier));
            start = end;
        }

        for (Thread core : cores)
        {
            core.start();
        }
        for (Thread core : cores)
        {
            try
            { 
                core.join();
            }
            catch (InterruptedException ignored) {}
        }
    }

    private void printMatrix(int[][] matrix)
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

    private class Core implements Runnable
    {
        private final int startRow, endRow;
        private final CyclicBarrier barrier;

        Core(int start, int end, CyclicBarrier barrier)
        {
            this.startRow = start;
            this.endRow = end;
            this.barrier = barrier;
        }

        @Override
        public void run()
        {
            try
            {
                for (int i = startRow; i < endRow; i++)
                {
                    for (int j = 0; j < matrix[i].length; j++)
                    {
                        matrix[i][j] *= 2;
                    }
                }
                
                if (startRow == 0)
                {
                    stage = 1;
                }
                barrier.await();

                for (int i = startRow; i < endRow; i++)
                {
                    for (int j = 0; j < matrix[i].length; j++)
                    {
                        matrix[i][j] += 10;
                    }
                }
                if (startRow == 0) stage = 2;
                barrier.await();

                for (int i = startRow; i < endRow; i++)
                {
                    for (int j = 0; j < matrix[i].length; j++)
                    {
                        matrix[i][j] /= 3;
                    }
                }
                if (startRow == 0) stage = 3;
                barrier.await();

            } catch (InterruptedException | BrokenBarrierException e)
            {
                e.printStackTrace();
            }
        }
    }
}
