package cloud.ds.explorer.lambda.MedianTracker;

import DataStructures.TwoHeapMedian;
import Models.TwoHeaps.MedianTrackerInput;
import Repository.RedisConnection;
import cloud.ds.explorer.lambda.GatewayResponse;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import redis.clients.jedis.Jedis;
import util.HttpUtil;
import util.JsonUtil;

import java.util.Map;

public class ValuePostHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {
	@Override
	public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LambdaLogger logger = context.getLogger();

		String body = (String)input.get("body");
		logger.log("Body is: "+body);

		MedianTrackerInput inputValue = JsonUtil.parseJson(body, MedianTrackerInput.class);

		addValue(inputValue.getValue());

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return HttpUtil.generateResponse(200, gson.toJson("OK"));
	}

	private void addValue(int value) {
		try {
			Jedis jedis = RedisConnection.getJedisFromPool();
			var twoHeaps = new TwoHeapMedian(jedis);
			twoHeaps.addNum(value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
