package Repository;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPooled;

import java.util.UUID;

public class RedisConnection {
	private static RedisClient redisClient;
	private static StatefulRedisConnection<String, String> connection;
	// Track if AWS is reusing the same lambda instance
	private static String lambdaPoolId;
	private static JedisPool pool;

	static {
		lambdaPoolId = UUID.randomUUID().toString();
		String redisHost = "host.docker.internal";
		int redisPort = 6379;
		String redisPassword = "redispw";
		JedisPoolConfig config = new JedisPoolConfig();
		config.setMaxTotal(128);
		config.setMaxIdle(64);
		config.setMinIdle(16);
		pool = new JedisPool(config, redisHost, redisPort, 2000, redisPassword);
	}

	public static Jedis getJedisFromPool() {
		System.out.println("Pool ID: " + lambdaPoolId);
		return pool.getResource();
	}

	public static void closeConnection() {
		if(pool != null)
			pool.close();
	}
}
