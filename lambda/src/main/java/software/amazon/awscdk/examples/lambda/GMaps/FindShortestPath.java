package software.amazon.awscdk.examples.lambda.GMaps;
import DataStructures.Dijkstra;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindShortestPath implements RequestHandler<Map<String, Object>, GatewayResponse> {
    private final MapsRepository mapsRepository;
    public FindShortestPath() {
        this.mapsRepository = DIFactory.createMapsRepository("GeoMaps");
    }

    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside software.amazon.awscdk.examples.lambda: findShortestPath "+input.getClass()+ " data:"+input);

        String body = (String)input.get("body");
        logger.log("Body is:"+body);

        Map<String, Object> pathParameters = (Map<String, Object>)input.get("pathParameters");
        String src = (String)pathParameters.get("src");
        String dest = (String)pathParameters.get("dest");

        String x1 = src.split(",")[0];
        String y1 = src.split(",")[1];

        String x2 = dest.split(",")[0];
        String y2 = dest.split(",")[1];

        Point srcPoint = new Point(Integer.parseInt(x1), Integer.parseInt(y1));
        Point destPoint = new Point(Integer.parseInt(x2), Integer.parseInt(y2));

        var output = execute(srcPoint, destPoint);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        var jsonOutput = gson.toJson(output);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new GatewayResponse(jsonOutput, headers, 200);
    }

    private List<Point> execute(Point src, Point dest) {

        var srcPointWithAdjList =  mapsRepository.getPoint(src.x, src.y);
        var destPointWithAdjList = mapsRepository.getPoint(dest.x, dest.y);

        // TODO: Refactor - assumes that response contains at least one point
//        var srcQuadId = srcPointWithAdjList.get(0).quadId;
//        var destQuadId = destPointWithAdjList.get(0).quadId;
//        var baseQuadId = StringUtil.getCommonStartingCharacters(srcQuadId, destQuadId);
//
//        // Query all points in the base quad
//        var pointsInBaseQuad = mapsRepository.getPointsInQuad(baseQuadId);

        // TESTING
        Map<Point, List<PointEdge>> pointsInBaseQuad = new HashMap<>();
        // Add A
        var pointA = new Point(0, 4);
        var pointAEdges = new ArrayList<PointEdge>();
        pointAEdges.add(new PointEdge(new Point(1, 0), 2));
        pointAEdges.add(new PointEdge(new Point(0, 0), 4));
        pointAEdges.add(new PointEdge(new Point(3, 4), 3));
        pointsInBaseQuad.put(pointA, pointAEdges);

        // Add B
        var pointB = new Point(3, 4);
        var pointBEdges = new ArrayList<PointEdge>();
        pointBEdges.add(new PointEdge(new Point(0, 4), 3));
        pointBEdges.add(new PointEdge(new Point(1, 0), 6));
        pointBEdges.add(new PointEdge(new Point(4, 3), 1));
        pointsInBaseQuad.put(pointB, pointBEdges);

        // Add C
        var pointC = new Point(0, 0);
        var pointCEdges = new ArrayList<PointEdge>();
        pointCEdges.add(new PointEdge(new Point(0, 4), 4));
        pointCEdges.add(new PointEdge(new Point(1, 0), 1));
        pointsInBaseQuad.put(pointC, pointCEdges);

        // Add D
        var pointD = new Point(1, 0);
        var pointDEdges = new ArrayList<PointEdge>();
        pointDEdges.add(new PointEdge(new Point(0, 0), 1));
        pointDEdges.add(new PointEdge(new Point(0, 4), 2));
        pointDEdges.add(new PointEdge(new Point(3, 4), 6));
        pointDEdges.add(new PointEdge(new Point(4, 3), 5));
        pointsInBaseQuad.put(pointD, pointDEdges);

        // Add E
        var pointE = new Point(4, 3);
        var pointEEdges = new ArrayList<PointEdge>();
        pointEEdges.add(new PointEdge(new Point(1, 0), 5));
        pointEEdges.add(new PointEdge(new Point(3, 4), 1));
        pointsInBaseQuad.put(pointE, pointEEdges);
        // TESTING

        var dijktra = new Dijkstra(pointsInBaseQuad);

        var shortestPath = dijktra.findShortestPath(pointA, pointE);

        for(var point : shortestPath)
            System.out.println(point.x + "," + point.y);

        return shortestPath;
    }
}
