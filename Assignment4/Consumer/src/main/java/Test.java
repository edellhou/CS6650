import redis.clients.jedis.JedisPooled;

public class Test {
    public static void main(String[] args){
        JedisPooled jedis = new JedisPooled("54.189.195.124", 6379);
        String message = "6";
        jedis.incrBy("wallet", Long.parseLong(message) * 10);
    }
}
