package kz.kaznu.course.lab1;



public class ParallelExecutionDemo {
// Последовательное выполнение
public static void sequentialExecution() {
long start = System.currentTimeMillis();
heavyTask("Задача-1");
heavyTask("Задача-2");
heavyTask("Задача-3");
long end = System.currentTimeMillis();
System.out.println("Последовательно: " + (end - start) + " мс");
}
// Параллельное выполнение
public static void parallelExecution() throws InterruptedException {
long start = System.currentTimeMillis();
Thread t1 = new Thread(() -> heavyTask("Задача-1"));
Thread t2 = new Thread(() -> heavyTask("Задача-2"));
Thread t3 = new Thread(() -> heavyTask("Задача-3"));
t1.start();
t2.start();
t3.start();
t1.join(); // Ждем завершения всех
t2.join();
t3.join();
long end = System.currentTimeMillis();
System.out.println("Параллельно: " + (end - start) + " мс");
}
private static void heavyTask(String taskName) {
System.out.println(taskName + " начата в потоке: " +
Thread.currentThread().getName());
// Имитация тяжелой работы
try {
Thread.sleep(2000); // 2 секунды
} catch (InterruptedException e) {
Thread.currentThread().interrupt();
}
System.out.println(taskName + " завершена");
}
public static void main(String[] args) throws InterruptedException {
System.out.println("=== Сравнение производительности ===");
System.out.println("Доступно ядер: " + Runtime.getRuntime().availableProcessors());
System.out.println("\n1. Последовательное выполнение:");
sequentialExecution();
System.out.println("\n2. Параллельное выполнение:");
parallelExecution();
}
}
