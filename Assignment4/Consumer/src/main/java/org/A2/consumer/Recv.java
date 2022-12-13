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
                            //message = skierID + DELIMITER + resortID + DELIMITER + seasonID + DELIMITER + dayID + DELIMITER + liftTime + DELIMITER + liftID

                            String[] messageParts = message.split(DELIMITER);
                            // data model one, hyperLogLog
                            // get number of unique skiers at resort/season/day, resort and season are all constant
                            StringBuilder keyOne= new StringBuilder();
                            keyOne.append("resortID:7:seasonID:2022:dayID:");
                            keyOne.append(messageParts[3]);
                            jedis.pfadd(keyOne.toString(), messageParts[0]);

                            //data model two, String, use increBy
                            //get ski day vertical for a skier
                            StringBuilder keyTwo= new StringBuilder();
                            keyTwo.append("resortID:7:seasonID:2022:dayID:");
                            keyTwo.append(messageParts[3]);
                            keyTwo.append(":");
                            keyTwo.append("skierID:");
                            keyTwo.append(messageParts[0]);
                            jedis.incrBy(keyTwo.toString(), Long.parseLong(messageParts[5]) * 10);

                            //data model three, String, use increBy
                            StringBuilder keyThree= new StringBuilder();
                            keyThree.append("resortID:7:seasonID:2022:skierID:");
                            keyThree.append(messageParts[0]);
                            jedis.incrBy(keyThree.toString(), Long.parseLong(messageParts[5]) * 10);
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
