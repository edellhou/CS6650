

import org.apache.http.HttpException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.util.concurrent.CountDownLatch;

import java.io.*;

public class HttpClient {
    private static String url = "http://localhost:8080/Lab3_war_exploded/HelloWorld/1";
    final static private int NUMTHREADS = 100;

    public static void main(String[] args) throws InterruptedException {


        // count down latch
        CountDownLatch  completed = new CountDownLatch(NUMTHREADS);

        System.out.println("before starting thread");
        // Execute the method.
        Long start =  System.currentTimeMillis();
        for (int i = 0; i < NUMTHREADS; i++) {

            // lambda runnable creation - interface only has a single method so lambda works fine
            Runnable thread =  () -> {
                CloseableHttpClient client = HttpClients.createDefault();
                HttpGet get = new HttpGet(url);
                try {
                    client.execute(get);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                completed.countDown();

            };

            new Thread(thread).start();
        }

        completed.await();
        Long end =  System.currentTimeMillis();
        Long TimeElapse = end - start;
        System.out.println("Total time to run is " + TimeElapse);


    }
}
