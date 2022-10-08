package PartOne;

import io.swagger.client.ApiClient;
import io.swagger.client.api.SkiersApi;

import java.util.concurrent.*;

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

        //Start Time
        Long start =  System.currentTimeMillis();

        //starting producer to produce messages in queue
        new Thread(producer).start();

        //starting consumer to consume messages from queue

        ExecutorService consumerPool = Executors.newFixedThreadPool(maxThread);
        for (int i = 0; i < 32; i++) {
            PostRunner consumer = new PostRunner(queue, nextPhasesSignal, requestCountDown, successCountDown, numRequest, MAXCAPACITY);
            consumerPool.execute(consumer);

        }

        nextPhasesSignal.await();
        for (int i = 0; i < maxThread; i++) {
            PostRunner consumer = new PostRunner(queue, nextPhasesSignal, requestCountDown,successCountDown, numRequest, MAXCAPACITY);
            consumerPool.execute(consumer);
        }

        requestCountDown.await();
        Long end =  System.currentTimeMillis();
        consumerPool.shutdown();
        consumerPool.awaitTermination( 10, TimeUnit.SECONDS);
        Long wallTime = (end - start) ;

        System.out.println("total successful requests: " +  (MAXCAPACITY - successCountDown.getCount()));
        System.out.println("total failed requests: " + successCountDown.getCount());
        System.out.println("total time used in millisecond: " + wallTime);
        System.out.println("total throughput in requests per second: " + (double) MAXCAPACITY/wallTime * 1000);


    }
}