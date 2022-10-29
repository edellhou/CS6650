package PartTwo;


import io.swagger.client.model.LiftRide;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;

public class GeneratorRunner implements Runnable {

    private LinkedBlockingQueue<Generator> queue;
    private int count = 0;
    private final int maxCapacity;

    public GeneratorRunner (LinkedBlockingQueue<Generator> q, int maxCapacity){
        this.queue = q;
        this.maxCapacity = maxCapacity;
    }
    @Override
    public void run() {
        //produce messages
        try {
            while (count < this.maxCapacity){
                Integer resortID = ThreadLocalRandom.current().nextInt(1, 11);
                String seasonID = "2022";
                String DayID = "1";
                Integer skierID = ThreadLocalRandom.current().nextInt(1, 100001);
                Integer liftID = ThreadLocalRandom.current().nextInt(1, 41);
                Integer time = ThreadLocalRandom.current().nextInt(1, 361);
                LiftRide newRide = new LiftRide();
                newRide.setTime(time);
                newRide.setLiftID(liftID);
                Generator generator = new Generator(newRide, resortID, seasonID, DayID, skierID);
                queue.put(generator);
                count += 1;
            }
            System.out.println("finished producing");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
