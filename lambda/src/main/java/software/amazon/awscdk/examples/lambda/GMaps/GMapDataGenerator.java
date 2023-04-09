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

// Description:  This lambda function generates a random graph of points and edges
// Input: Takes in a x1,y1,x2,y2 and a depth value. The x1,y1,x2,y2 values are the coordinates of the main quadtree
        // The depth value is the depth of the quadtree (i.e. how many times we divide the quadtree)
// Logic:
    // 1. Using the x,y coordinates and the depth value, we create a quadtree and list of quadIds
    // 2. For each of the leaf quadIds we generate random numbers and assign them to the quadId
    // 3. Create a Minimum Spanning Tree using the random points
    // 4. Save the points and edges that define the MST to the database (DynamoDB)
// TODO: The MST does not guarantee that a group of points are connected. Add logic to ensure that subgraphs are connected.
// TODO: IDEA: Create an MST for each QuadId (subgraph) and then connect the subgraphs to each other
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

                String quadId = QuadTree.findQuadId(x, y, quadTree.root);

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
