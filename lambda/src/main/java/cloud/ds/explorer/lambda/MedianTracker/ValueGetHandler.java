package cloud.ds.explorer.lambda.MedianTracker;

import DataStructures.TwoHeapMedian;
import Repository.RedisConnection;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import redis.clients.jedis.Jedis;
import software.amazon.awscdk.examples.lambda.GatewayResponse;
import util.HttpUtil;

import java.util.Map;

public class ValueGetHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {
	@Override
	public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LambdaLogger logger = context.getLogger();

		try {
			Jedis jedis = RedisConnection.getJedisFromPool();
			var twoHeaps = new TwoHeapMedian(jedis);

			var median = twoHeaps.findMedian();

			return HttpUtil.generateResponse(200, String.valueOf(median));
		}
		catch (Exception e) {
			e.printStackTrace();
			return HttpUtil.generateResponse(500, e.getMessage());
		}
	}
}
