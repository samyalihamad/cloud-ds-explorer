package Repository;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisConnection {
	private static RedisClient redisClient;
	private static StatefulRedisConnection<String, String> connection;

	static {
		String redisHost = "host.docker.internal";
		int redisPort = 6379;

		RedisURI redisURI = RedisURI.create(redisHost, redisPort);
		redisClient = RedisClient.create(redisURI);

		connection = redisClient.connect();
	}

	public static RedisCommands<String, String> getCommands() {
		return connection.sync();
	}

	public static void closeConnection() {
		connection.close();
		redisClient.shutdown();
	}
}
