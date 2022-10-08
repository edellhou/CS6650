package CS6500_Assignment.PartOne;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.api.SkiersApi;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;

public class PostRunner implements Runnable{
    private  BlockingQueue<RandomGenerator> queue;
    private CountDownLatch countDownLatch;

    private int numRequest;
    private final String basePath = "http://localhost:8080/Upic_war_exploded/";

    public PostRunner(BlockingQueue<RandomGenerator> q, CountDownLatch latch, int numRequest) {
        this.queue = q;
        this.countDownLatch = latch;
        this.numRequest = numRequest;
    }

    @Override
    public void run() {
        try {
            while (queue.remainingCapacity() != 200000) {
                try {
                    for (int i = 0; i < numRequest; i ++) {
                        consume(queue.take());
                    }
                    this.countDownLatch.countDown();
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void consume(RandomGenerator generator) throws ApiException {

        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(basePath);
        SkiersApi skiersApi = new SkiersApi(apiClient);
        skiersApi.writeNewLiftRide(generator.getRide(), generator.getResortID(), generator.getSeasonID(), generator.getDayID(), generator.getSkierID());

    }
}
