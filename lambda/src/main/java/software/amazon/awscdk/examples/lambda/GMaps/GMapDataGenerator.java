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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awscdk.examples.lambda.GatewayResponse;
import util.StringUtil;

import java.util.*;
import java.util.stream.Collectors;

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
        return new GatewayResponse("success", headers, 200);
    }

    private List<GraphEdge> execute(String body) {
        QuadTree quadTree = new QuadTree(0, 0, 20, 20, 2);
        Set<Point> points = new HashSet<>();
        Map<String, Set<GraphEdge>> quadPointGraphEdges = new HashMap<>();

        Random random = new Random();

        for(String quadRange : quadTree.quadRanges) {
            String[] quadValues = quadRange.split("[;,]");
            int x1 = Integer.parseInt(quadValues[0]);
            int y1 = Integer.parseInt(quadValues[1]);
            int x2 = Integer.parseInt(quadValues[2]);
            int y2 = Integer.parseInt(quadValues[3]);

            String quadId = QuadTree.findQuadId(x1 + 1, y1 + 1, quadTree.root);
            List<Point> subPoints = new ArrayList<>();
            for(int i = 0; i < 2; i++) {
                int x = random.nextInt(x2 - x1 - 1) + x1 + 1;
                int y = random.nextInt(y2 - y1 - 1) + y1 + 1;


                Point point = new Point(x, y, quadId);

                if(subPoints.contains(point)) {
                    i--;
                    continue;
                }

                subPoints.add(point);
            }

            points.addAll(subPoints);

            PrimMST primMSTSubQuad = new PrimMST(subPoints);
            var subQuadEdges = primMSTSubQuad.getMST();
            quadPointGraphEdges.put(quadId, subQuadEdges);
        }

        for(var point : quadPointGraphEdges.entrySet()) {
            var quadId = point.getKey();
            var currPoints = point.getValue();

            //TODO: Try to pick points in neighboring quads that are closest to the current quad
            //  To connect two minimum spanning trees (MSTs) while maintaining their structure, you can follow these steps:
            //  1. Identify the MSTs. Let's call them MST1 and MST2.
            //  2. Find the minimum-weight edge connecting the two MSTs. To do this, iterate through all the edges between the
            // vertices in MST1 and MST2, and choose the edge with the smallest weight.
            // 3. Add this minimum-weight edge to the MST1 (or MST2) to connect the two MSTs
//            int closestNorthY = Integer.MIN_VALUE;
//            int closestSouthY = Integer.MAX_VALUE;
//            int closestEastX = Integer.MIN_VALUE;;
//            int closestWestX = Integer.MAX_VALUE;
//
//            for(var pt : currPoints) {
//                if(pt.a.x > closestEastX)
//                    closestEastX = pt.a.x;
//
//                if(pt.b.x > closestEastX)
//                    closestEastX = pt.b.x;
//
//                if(pt.a.x < closestWestX)
//                    closestWestX = pt.a.x;
//
//                if(pt.b.x < closestWestX)
//                    closestWestX = pt.b.x;
//
//                if(pt.a.y > closestNorthY)
//                    closestNorthY = pt.a.y;
//
//                if(pt.b.y > closestNorthY)
//                    closestNorthY = pt.b.y;
//
//                if(pt.a.y < closestSouthY)
//                    closestSouthY = pt.a.y;
//
//                if(pt.a.y < closestSouthY)
//                    closestSouthY = pt.a.y;
//            }

            var quadNeighbors = StringUtil.getNeighborQuad(quadId);

            // TODO: If we connect two quads we should not connect them again in the next iteration
            for(var neighbor : quadNeighbors.entrySet()) {
                if(neighbor.getKey() == "north" && quadPointGraphEdges.containsKey(neighbor.getValue())) {
                    var northPoints = quadPointGraphEdges.get(neighbor.getValue());

                    var northPoint = northPoints.stream().findFirst().get().a;
                    var currPoint = currPoints.stream().findFirst().get().a;

                    int weight = Math.abs(northPoint.x - currPoint.x) + Math.abs(northPoint.y - currPoint.y);
                    GraphEdge edge = new GraphEdge(northPoint, currPoint, weight);
                    currPoints.add(edge);
                }

                if(neighbor.getKey() == "south" && quadPointGraphEdges.containsKey(neighbor.getValue())) {
                    var southPoints = quadPointGraphEdges.get(neighbor.getValue());

                    var southPoint = southPoints.stream().findFirst().get().a;
                    var currPoint = currPoints.stream().findFirst().get().a;

                    int weight = Math.abs(southPoint.x - currPoint.x) + Math.abs(southPoint.y - currPoint.y);
                    GraphEdge edge = new GraphEdge(southPoint, currPoint, weight);
                    currPoints.add(edge);
                }

                if(neighbor.getKey() == "east" && quadPointGraphEdges.containsKey(neighbor.getValue())) {
                    var eastPoints = quadPointGraphEdges.get(neighbor.getValue());

                    var eastPoint = eastPoints.stream().findFirst().get().a;
                    var currPoint = currPoints.stream().findFirst().get().a;

                    int weight = Math.abs(eastPoint.x - currPoint.x) + Math.abs(eastPoint.y - currPoint.y);
                    GraphEdge edge = new GraphEdge(eastPoint, currPoint, weight);
                    currPoints.add(edge);
                }

                if(neighbor.getKey() == "west" && quadPointGraphEdges.containsKey(neighbor.getValue())) {
                    var westPoints = quadPointGraphEdges.get(neighbor.getValue());

                    var westPoint = westPoints.stream().findFirst().get().a;
                    var currPoint = currPoints.stream().findFirst().get().a;

                    int weight = Math.abs(westPoint.x - currPoint.x) + Math.abs(westPoint.y - currPoint.y);
                    GraphEdge edge = new GraphEdge(westPoint, currPoint, weight);
                    currPoints.add(edge);
                }
            }
        }

        List<GraphEdge> outputEdges = new ArrayList<>();
        for(var quadEdges : quadPointGraphEdges.entrySet()) {
            outputEdges.addAll(quadEdges.getValue());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        System.out.println(gson.toJson(outputEdges));


        Map<Point, Set<PointEdge>> adjacencyList = new HashMap<>();
        for(var point : points) {
            adjacencyList.put(point, new HashSet<>());
        }

        for(var edge : outputEdges) {
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

        return outputEdges;
    }
}
