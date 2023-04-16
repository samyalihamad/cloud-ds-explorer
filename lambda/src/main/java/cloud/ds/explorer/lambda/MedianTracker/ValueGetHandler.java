package cloud.ds.explorer.lambda.MedianTracker;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awscdk.examples.lambda.GatewayResponse;
import util.HttpUtil;

import java.util.Map;

public class ValueGetHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {
	@Override
	public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LambdaLogger logger = context.getLogger();


		int value = 10;

		return HttpUtil.generateResponse(200, String.valueOf(value));

	}
}
