package PartTwo;

import java.io.*;
import java.util.concurrent.*;

import static java.util.Arrays.sort;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        int MAXCAPACITY = 2000;
        int maxThread = 10;
        int numRequest = 200;

        //Creating BlockingQueue of size 10
        LinkedBlockingQueue<Generator> queue = new LinkedBlockingQueue<>(MAXCAPACITY);
        CountDownLatch nextPhasesSignal = new CountDownLatch(1);
        CountDownLatch requestCountDown = new CountDownLatch(MAXCAPACITY);
        CountDownLatch successCountDown = new CountDownLatch(MAXCAPACITY);

        GeneratorRunner producer = new GeneratorRunner(queue, MAXCAPACITY);

        LinkedBlockingQueue<Long> runTimeQueue = new LinkedBlockingQueue<>(MAXCAPACITY);
        LinkedBlockingQueue<String> logQueue = new LinkedBlockingQueue<>(MAXCAPACITY);

        //Start Time
        Long start = System.currentTimeMillis();

        //starting producer to produce messages in queue
        new Thread(producer).start();

        //starting consumer to consume messages from queue

        ExecutorService consumerPool = Executors.newFixedThreadPool(maxThread);
        for (int i = 0; i < maxThread; i++) {
            PostRunner consumer = new PostRunner(queue, runTimeQueue, logQueue, nextPhasesSignal, requestCountDown, successCountDown, numRequest, MAXCAPACITY);
            consumerPool.execute(consumer);

        }

        nextPhasesSignal.await();
        System.out.println("Done");
    }
}
