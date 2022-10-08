package PartTwo;

import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SkiersApi;

import java.util.LongSummaryStatistics;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

public class PostRunner implements Runnable{
    private LinkedBlockingQueue<Generator> queue;
    private LinkedBlockingQueue<Long> runTimeQueue;
    private LinkedBlockingQueue<String> logQueue;
    private CountDownLatch threadCountDown;

    private CountDownLatch requestCountDown;

    private CountDownLatch successCountDown;




    private final int maxCapacity;

    private int numRequest;
    private final String localBasePath = "http://localhost:8080/UpicServlet_war_exploded/";
    private final String remoteBasePath = "http://34.221.46.75:8080/UpicSpring-0.0.1-SNAPSHOT/";
            //"http://34.221.46.75:8080/UpicServlet_war/";

    public PostRunner(LinkedBlockingQueue<Generator> q, LinkedBlockingQueue<Long> runTimeQueue,LinkedBlockingQueue<String> logQueue,CountDownLatch threadCountDown,  CountDownLatch requestCountDown,CountDownLatch successCountDown, int numRequest, int maxCapacity) {
        this.queue = q;
        this.runTimeQueue = runTimeQueue;
        this.logQueue = logQueue;
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
        apiClient.setBasePath(remoteBasePath);
        SkiersApi skiersApi = new SkiersApi(apiClient);

        long start = System.currentTimeMillis();

        ApiResponse<Void> res = skiersApi.writeNewLiftRideWithHttpInfo(generator.getRide(), generator.getResortID(), generator.getSeasonID(), generator.getDayID(), generator.getSkierID());
        this.requestCountDown.countDown();
        int resStatusCode = res.getStatusCode();
        if(resStatusCode  == 201 || resStatusCode  == 200){
           this.successCountDown.countDown();
      } else{
            int j = 0;
            while(j < 5 && (resStatusCode != 201 || resStatusCode != 200)){
                res = skiersApi.writeNewLiftRideWithHttpInfo(generator.getRide(), generator.getResortID(), generator.getSeasonID(), generator.getDayID(), generator.getSkierID());
                resStatusCode = res.getStatusCode();
                j++;
            }
        }

        long end = System.currentTimeMillis();
        long latency = end - start;
        runTimeQueue.add(latency);

        StringBuilder builder = new StringBuilder();
        builder.append(start).append(",")
                .append("POST").append(",")
                .append(latency).append(",").append(resStatusCode).append("\n");
        logQueue.add(builder.toString());

    }
}
