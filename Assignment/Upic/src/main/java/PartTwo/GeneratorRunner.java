package PartTwo;


import java.util.concurrent.LinkedBlockingQueue;

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
                Generator generator = new Generator();
                generator.generate();
                queue.put(generator);
                count += 1;
            }
            System.out.println("finished producing");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
