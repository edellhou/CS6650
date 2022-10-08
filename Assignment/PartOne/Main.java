package CS6500_Assignment.PartOne;

import CS6500_Assignment.PartOne.GeneratorRunner;
import CS6500_Assignment.PartOne.PostRunner;
import CS6500_Assignment.PartOne.RandomGenerator;
import io.swagger.client.ApiClient;
import io.swagger.client.api.SkiersApi;

import java.util.concurrent.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        //Creating BlockingQueue of size 10
        BlockingQueue<RandomGenerator> queue = new LinkedBlockingQueue<>(200000);
        CountDownLatch nextPhasesSignal = new CountDownLatch(1);
        GeneratorRunner producer = new GeneratorRunner(queue);

        //Start Time
        Long start =  System.currentTimeMillis();

        //starting producer to produce messages in queue
        new Thread(producer).start();
        //starting consumer to consume messages from queue

        ExecutorService consumerPoolOne = Executors.newFixedThreadPool(32);
        for (int i = 0; i < 32; i++) {
            PostRunner consumer = new PostRunner(queue, nextPhasesSignal, 1000);
            consumerPoolOne.execute(consumer);
            nextPhasesSignal.countDown();
        }
        nextPhasesSignal.await();


        ExecutorService consumerPoolTwo = Executors.newFixedThreadPool(168);
        for (int i = 0; i < 168; i++) {
            PostRunner consumerTwo = new PostRunner(queue, nextPhasesSignal, 1000);
            consumerPoolTwo.execute(consumerTwo);
        }

        consumerPoolOne.shutdown();
        consumerPoolOne.shutdown();

        Long end =  System.currentTimeMillis();
        Long wallTime = (end - start) ;
        System.out.println("Total time to run is " + wallTime + " seconds");
        System.out.println("Total throughput is " + 200000);


    }
}