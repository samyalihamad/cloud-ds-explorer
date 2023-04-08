package software.amazon.awscdk.examples.lambda.GMaps;
import Models.Point;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awscdk.examples.lambda.GatewayResponse;

import java.util.HashMap;
import java.util.Map;

public class FindShortestPath implements RequestHandler<Map<String, Object>, GatewayResponse> {
    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside software.amazon.awscdk.examples.lambda: findShortestPath "+input.getClass()+ " data:"+input);

        String body = (String)input.get("body");
        logger.log("Body is:"+body);

        var output = execute(body);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new GatewayResponse(output.toString(), headers, 200);
    }

    // TODO: Currently returning shortest distance, need to return shortest path
    private Integer execute(String body) {
        Point src = new Point(12, 13);
        Point dest = new Point(1, 18);



        return 0;
    }
}
