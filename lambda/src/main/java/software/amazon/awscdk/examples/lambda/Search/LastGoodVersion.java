package software.amazon.awscdk.examples.lambda.Search;

import DataStructures.BinarySearch;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awscdk.examples.lambda.GatewayResponse;

import java.util.HashMap;
import java.util.Map;

public class LastGoodVersion implements RequestHandler<Map<String,Object>, GatewayResponse> {
	@Override
	public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LambdaLogger logger = context.getLogger();
		logger.log("Inside software.amazon.awscdk.examples.lambda: getOneItem "+input.getClass()+ " data:"+input);

		String body = (String)input.get("body");
		logger.log("Body is:"+body);

		var output = execute(body);

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		return new GatewayResponse(output.toString(), headers, 200);
	}

	private Integer execute(String body) {
		int[] input = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

		BinarySearch binarySearch = new BinarySearch(input);
		return binarySearch.search(3);
	}
}
