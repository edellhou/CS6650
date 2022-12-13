package PartTwo;

import java.io.*;
import java.util.concurrent.*;

import static java.util.Arrays.sort;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        int MAXCAPACITY = 200000;
        int maxThread = 168;
        int numRequest = 1000;

        //Creating BlockingQueue of size 10
        LinkedBlockingQueue<Generator> queue = new LinkedBlockingQueue<>(MAXCAPACITY);
        CountDownLatch nextPhasesSignal = new CountDownLatch(1);
        CountDownLatch requestCountDown= new CountDownLatch(MAXCAPACITY);
        CountDownLatch successCountDown= new CountDownLatch(MAXCAPACITY);

        GeneratorRunner producer = new GeneratorRunner(queue, MAXCAPACITY);

        LinkedBlockingQueue<Long> runTimeQueue = new LinkedBlockingQueue<>(MAXCAPACITY);
        LinkedBlockingQueue<String> logQueue = new LinkedBlockingQueue<>(MAXCAPACITY);

        //Start Time
        Long start =  System.currentTimeMillis();

        //starting producer to produce messages in queue
        new Thread(producer).start();

        //starting consumer to consume messages from queue

        ExecutorService consumerPool = Executors.newFixedThreadPool(maxThread);
        for (int i = 0; i < 32; i++) {
            PostRunner consumer = new PostRunner(queue, runTimeQueue, logQueue, nextPhasesSignal, requestCountDown, successCountDown, numRequest, MAXCAPACITY);
            consumerPool.execute(consumer);

        }

        nextPhasesSignal.await();
        for (int i = 0; i < maxThread; i++) {
            PostRunner consumer = new PostRunner(queue, runTimeQueue, logQueue, nextPhasesSignal, requestCountDown,successCountDown, numRequest, MAXCAPACITY);
            consumerPool.execute(consumer);
        }

        requestCountDown.await();
        Long end =  System.currentTimeMillis();
        consumerPool.shutdown();
        consumerPool.awaitTermination( 10, TimeUnit.SECONDS);
        Long wallTime = (end - start) ;
        Object[] runTimeArray = runTimeQueue.toArray();
        sort(runTimeArray);
        long totalResponseTime = 0;
        for(int i = 0; i < MAXCAPACITY; i++){
            totalResponseTime = totalResponseTime + (long) runTimeArray[i];
        }
        long mean = totalResponseTime / MAXCAPACITY;
        System.out.println("Single request mean response time in millisecond: " + mean);
        System.out.println("Single request Median response time in millisecond: " + runTimeArray[MAXCAPACITY / 2 - 1]);
        System.out.println("Single request p99 response time in millisecond: " + runTimeArray[MAXCAPACITY / 100 * 99 - 1]);
        System.out.println("Single request Max response time in millisecond: " + runTimeArray[MAXCAPACITY - 1]);
        System.out.println("total successful requests: " +  (MAXCAPACITY - successCountDown.getCount()));
        System.out.println("total failed requests: " + successCountDown.getCount());
        System.out.println("total time used in millisecond: " + wallTime);
        System.out.println("total throughput in requests per second: " + (double) MAXCAPACITY/wallTime * 1000);

        File file = new File("log.csv");
        try {
            // create FileWriter object with file as parameter
            FileOutputStream outputStream = new FileOutputStream (file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            while (!logQueue.isEmpty()) {
                writer.write(logQueue.take());
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}