package org.A2.consumer;

import redis.clients.jedis.Jedis;

public class Main {
    public static void main(String[] args) {

        try (Jedis jedis = new Jedis("54.201.255.201",6379)) {
            System.out.println("Connected to jedis " + jedis.ping());
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}