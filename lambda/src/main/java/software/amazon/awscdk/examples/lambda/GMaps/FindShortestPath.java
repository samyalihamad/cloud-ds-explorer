package software.amazon.awscdk.examples.lambda.GMaps;

import DataStructures.QuadTree;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awscdk.examples.lambda.GatewayResponse;
import util.JsonUtil;

import java.util.HashMap;
import java.util.List;
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
        QuadTree quadTree = new QuadTree(0, 0, 1024, 1024, 4, false);

//        System.out.println(quadTree.root.x1 + " " + quadTree.root.y1 + " " + quadTree.root.x2 + " " + quadTree.root.y2);
//        System.out.println("(511, 1023) -> Quad ID: " + quadTree.findQuadId(511, 1023));
//        System.out.println("(1, 513) -> Quad ID: " + quadTree.findQuadId(1, 513));
        return 0;
    }
}
