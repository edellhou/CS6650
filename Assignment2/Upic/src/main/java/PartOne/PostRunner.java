package PartOne;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class PostRunner implements Runnable{
    private LinkedBlockingQueue<Generator> queue;
    private CountDownLatch threadCountDown;

    private CountDownLatch requestCountDown;

    private CountDownLatch successCountDown;




    private final int maxCapacity;

    private int numRequest;
    private final String localBasePath = "http://localhost:8080/UpicServlet_war_exploded/";
    private final String remoteBasePath = "http://34.221.46.75:8080/UpicSpring-0.0.1-SNAPSHOT/";

    public PostRunner(LinkedBlockingQueue<Generator> q, CountDownLatch threadCountDown,  CountDownLatch requestCountDown,CountDownLatch successCountDown, int numRequest, int maxCapacity) {
        this.queue = q;
        this.threadCountDown = threadCountDown;
        this.numRequest = numRequest;
        this.maxCapacity = maxCapacity;
        this.requestCountDown = requestCountDown;
        this.successCountDown = successCountDown;
    }

    @Override
    public void run() {
            try {
                for (int i = 0; i < numRequest; i ++) {
                    consume(queue.poll());
                }
                this.threadCountDown.countDown();
            } catch (ApiException e) {
                throw new RuntimeException(e);
            }


    }

    public void consume(Generator generator) throws ApiException {
        ApiClient apiClient = new ApiClient();
        apiClient.setBasePath(localBasePath);
        SkiersApi skiersApi = new SkiersApi(apiClient);
        ApiResponse<Void> res = skiersApi.writeNewLiftRideWithHttpInfo(generator.getRide(), generator.getResortID(), generator.getSeasonID(), generator.getDayID(), generator.getSkierID());
        this.requestCountDown.countDown();
        if(res.getStatusCode() == 201 || res.getStatusCode() == 200){
           this.successCountDown.countDown();
      } else{
            int j = 0;
            while(j < 5 && (res.getStatusCode() != 201 || res.getStatusCode() != 200)){
                res = skiersApi.writeNewLiftRideWithHttpInfo(generator.getRide(), generator.getResortID(), generator.getSeasonID(), generator.getDayID(), generator.getSkierID());
                j++;
            }
        }


    }
}
