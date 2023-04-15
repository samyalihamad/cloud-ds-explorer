package util;

import software.amazon.awscdk.examples.lambda.GatewayResponse;

import java.util.HashMap;
import java.util.Map;

public class HttpUtil {
	public static GatewayResponse generateResponse(int statusCode, String body) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json");
		return new GatewayResponse(body, headers, statusCode);
	}
}
