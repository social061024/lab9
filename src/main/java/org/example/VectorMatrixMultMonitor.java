package org.example;

class MatrixThread extends Thread {
    int a[];
    int mc[][];
    int lo, hi, threadNumber;
    VectorMatrixMultMonitor mainThread;

    MatrixThread(int thNum, int a1[], int c1[][], int l, int h, VectorMatrixMultMonitor mt) {
        a = a1;
        mc = c1;
        lo = l;
        hi = h;
        threadNumber = thNum;
        mainThread = mt;
    }

    @Override
    public void run() {
        for (int i = lo; i < hi && i < mc.length; ++i) {
            for (int j = 0; j < mc[i].length; ++j) {
                a[i] = Math.max(a[i], mc[i][j]);
            }
        }
        mainThread.procDecrement();
    }
}

public class VectorMatrixMultMonitor extends Thread {
    int a[];
    int mc[][];
    private int procNum = 0;

    VectorMatrixMultMonitor(int a1[], int c1[][]) {
        a = a1;
        mc = c1;
    }

    @Override
    public synchronized void start() {
        // Створюємо потоки для обробки рядків матриці по 2 рядки на потік
        for (int i = 0; i < mc.length; i += 2) {
            (new MatrixThread(i + 1, a, mc, i, i + 2, this)).start();
            procIncrement();
        }
        super.start();
    }

    @Override
    public void run() {
        try {
            synchronized (this) {
                while (getProcNum() > 0) {
                    wait();
                }
            }
        } catch (InterruptedException ee) {
            System.out.println("InterruptedException: " + ee);
        }

        // Вивід результатів
        System.out.println("\nМаксимальні елементи рядків:");
        for (int i = 0; i < a.length; ++i) {
            System.out.print(a[i] + " ");
        }

        // Знаходимо загальний максимум
        int m = 0;
        for (int i = 0; i < a.length; ++i) {
            m = Math.max(m, a[i]);
        }
        System.out.println("\nМаксимальний елемент матриці: " + m);
    }

    public synchronized void procIncrement() {
        ++procNum;
    }

    public synchronized void procDecrement() {
        --procNum;
        notify();
    }

    public synchronized int getProcNum() {
        return procNum;
    }

    public static void main(String[] args) {
        int mSize = 6;
        int a[] = new int[mSize];
        int mc[][] = new int[mSize][mSize];

        // Генеруємо випадкові числа в діапазоні 100–999
        System.out.println("Матриця:");
        for (int i = 0; i < mSize; ++i) {
            a[i] = 0;
            for (int j = 0; j < mSize; ++j) {
                mc[i][j] = 100 + (int) (Math.random() * 900);
                System.out.print(mc[i][j] + " ");
            }
            System.out.println();
        }

        // Запускаємо багатопотокову обробку
        (new VectorMatrixMultMonitor(a, mc)).start();
    }
}
