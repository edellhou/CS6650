package org.A2.consumer;

import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import redis.clients.jedis.JedisPooled;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Recv {
    private final static String QUEUE_NAME = "skiersPost";
    private static final String EXCHANGE_NAME = "liftrideRecord";
    private static final String DELIMITER = " ";
    private static final int THREAD_POOL_SIZE = 32;

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        //factory.setHost("localhost");
        factory.setHost("52.12.44.5");
        factory.setUsername("admin");
        factory.setPassword("password");
        Connection connection = factory.newConnection();


        Runnable liftRideConsumer = () -> {
            JedisPooled jedis = new JedisPooled("54.189.195.124", 6379);
            try {
                    Channel channel = connection.createChannel();
                    channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
                    channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                    channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "");

                    channel.basicQos(1);
                    //System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

                    DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                        String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                        //System.out.println(" [x] Received '" + message + "'");
                        try {

                            String[] messageParts = message.split(DELIMITER);
                            StringBuilder keySkierAndDay = new StringBuilder();
                            StringBuilder keyResortAndDay = new StringBuilder();

                            keySkierAndDay.append("skierID:dayID:");
                            keySkierAndDay.append(messageParts[0]);
                            keySkierAndDay.append(":");
                            keySkierAndDay.append(messageParts[3]);



                            keyResortAndDay.append("resortID:dayID:");
                            keyResortAndDay.append(messageParts[1]);
                            keyResortAndDay.append(":");
                            keyResortAndDay.append(messageParts[3]);


                            jedis.sadd(keySkierAndDay.toString(), messageParts[5]);
                            jedis.pfadd(keyResortAndDay.toString(), messageParts[0]);

                            //storeEventToMap(message);
                        }catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                        }
                    };
                    channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> {
                    });

            } catch (IOException e) {
                e.printStackTrace();
            }


        };

        ExecutorService consumerPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        for (int i = 0; i < THREAD_POOL_SIZE; i++) {
            consumerPool.execute(liftRideConsumer);
        }
    }

}
