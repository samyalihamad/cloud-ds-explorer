package software.amazon.awscdk.examples.lambda.GMaps;

import DataStructures.PrimMST;
import DataStructures.QuadTree;
import Models.GraphEdge;
import Models.Point;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awscdk.examples.lambda.GatewayResponse;

import java.util.*;

public class GMapDataGenerator implements RequestHandler<Map<String, Object>, GatewayResponse> {
    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside software.amazon.awscdk.examples.lambda.GMaps: GMapDataLoader "+input.getClass()+ " data:"+input);

        String body = (String)input.get("body");
        logger.log("Body is:"+body);

        List<GraphEdge> output = execute(body);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new GatewayResponse(output.toString(), headers, 200);
    }

    private List<GraphEdge> execute(String body) {
        QuadTree quadTree = new QuadTree(0, 0, 20, 20, 2, true);
        Set<Point> points = new HashSet<>();

        Random random = new Random();

        for(String quadId : quadTree.quadIds) {
            String[] quadValues = quadId.split("[;,]");
            int x1 = Integer.parseInt(quadValues[0]);
            int y1 = Integer.parseInt(quadValues[1]);
            int x2 = Integer.parseInt(quadValues[2]);
            int y2 = Integer.parseInt(quadValues[3]);

            for(int i = 0; i < 2; i++) {
                int x = random.nextInt(x2 - x1 - 1) + x1 + 1;
                int y = random.nextInt(y2 - y1 - 1) + y1 + 1;

                Point point = new Point(x, y, quadId);
                points.add(point);
            }
        }

        // TODO: Refactor
        int[][] pointsArray = new int[points.size()][3];
        int i = 0;
        for(var point : points) {
            pointsArray[i][0] = point.x;
            pointsArray[i][1] = point.y;
            i++;
        }

        PrimMST primMST = new PrimMST(pointsArray);
        List<GraphEdge> edges = primMST.getMST();

        return edges;
    }
}
