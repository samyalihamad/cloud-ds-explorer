package Repository;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.JedisPooled;

import java.util.UUID;

public class RedisConnection {
	private static RedisClient redisClient;
	private static StatefulRedisConnection<String, String> connection;
	// Track if AWS is reusing the same lambda instance
	private static String lambdaPoolId;
	private static JedisPooled pool;

	static {
		lambdaPoolId = UUID.randomUUID().toString();
		String redisHost = "host.docker.internal";
		int redisPort = 6379;
		String redisPassword = "redispw";
		pool = new JedisPooled(new GenericObjectPoolConfig<>(), redisHost, redisPort, "default", redisPassword);
	}

	public static JedisPooled getJediClient() {
		System.out.println("Pool ID: " + lambdaPoolId);
		return pool;
	}

	public static void closeConnection() {
		if(pool != null)
			pool.close();
	}
}
