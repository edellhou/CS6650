package CS6500_Assignment.PartOne;

import java.util.concurrent.BlockingQueue;

public class GeneratorRunner implements Runnable {

    private BlockingQueue<RandomGenerator> queue;
    private int count = 0;
    private static final int MAXCount= 200000;

    public GeneratorRunner (BlockingQueue<RandomGenerator> q){
        this.queue = q;
    }
    @Override
    public void run() {
        //produce messages
        try {
                while (count < MAXCount){
                    RandomGenerator generator = new RandomGenerator();
                    generator.generate();
                    queue.put(generator);
                    count += 1;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

}
