package software.amazon.awscdk.examples.lambda.GMaps;

import DataStructures.PrimMST;
import DataStructures.QuadTree;
import Factories.DIFactory;
import Interfaces.MapsRepository;
import Models.GraphEdge;
import Models.Point;
import Models.PointEdge;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awscdk.examples.lambda.GatewayResponse;

import java.util.*;

public class GMapDataGenerator implements RequestHandler<Map<String, Object>, GatewayResponse> {
    private final MapsRepository mapsRepository;
    public GMapDataGenerator() {
        this.mapsRepository = DIFactory.createMapsRepository("GeoMaps");
    }
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
        QuadTree quadTree = new QuadTree(0, 0, 20, 20, 2);
        Set<Point> points = new HashSet<>();

        Random random = new Random();

        for(String quadRange : quadTree.quadRanges) {
            String[] quadValues = quadRange.split("[;,]");
            int x1 = Integer.parseInt(quadValues[0]);
            int y1 = Integer.parseInt(quadValues[1]);
            int x2 = Integer.parseInt(quadValues[2]);
            int y2 = Integer.parseInt(quadValues[3]);

            for(int i = 0; i < 2; i++) {
                int x = random.nextInt(x2 - x1 - 1) + x1 + 1;
                int y = random.nextInt(y2 - y1 - 1) + y1 + 1;

                // TODO: Refactor should be a static method
                String quadId = quadTree.findQuadId(x, y);

                Point point = new Point(x, y, quadId);
                points.add(point);
            }
        }

        List<Point> pointsList = new ArrayList<>();
        for(var point : points) {
            pointsList.add(point);
        }

        PrimMST primMST = new PrimMST(pointsList);
        List<GraphEdge> edges = primMST.getMST();

        Map<Point, ArrayList<PointEdge>> adjacencyList = new HashMap<>();
        for(var point : points) {
            adjacencyList.put(point, new ArrayList<>());
        }

        for(var edge : edges) {
            Point edgeA = edge.a;
            Point edgeB = edge.b;
            int weight = edge.weight;

            adjacencyList.get(edgeA).add(new PointEdge(edgeB, weight));
            adjacencyList.get(edgeB).add(new PointEdge(edgeA, weight));
        }


        // TODO: Refactor to save in batches
        for(var point : adjacencyList.entrySet()) {
            mapsRepository.savePoint(point.getKey(), point.getValue());
        }

        return edges;
    }
}
