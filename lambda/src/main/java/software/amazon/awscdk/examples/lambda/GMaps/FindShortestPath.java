package software.amazon.awscdk.examples.lambda.GMaps;
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

        var output = execute(body);

        System.out.println("Found point: " + output.x + ", " + output.y);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new GatewayResponse(output.toString(), headers, 200);
    }

    private Point execute(String body) {
        Point src = new Point(6, 3);
        Point dest = new Point(9, 9);

        var srcPointWithAdjList =  mapsRepository.getPoint(src.x, src.y);
        var destPointWithAdjList = mapsRepository.getPoint(dest.x, dest.y);

        // TODO: Refactor - assumes that response contains at least one point
        var srcQuadId = srcPointWithAdjList.get(0).quadId;
        var destQuadId = destPointWithAdjList.get(0).quadId;
        var baseQuadId = StringUtil.getCommonStartingCharacters(srcQuadId, destQuadId);

        // Query all points in the base quad
        var pointsInBaseQuad = mapsRepository.getPointsInQuad(baseQuadId);


        List<GraphEdge> graphEdges = new ArrayList<>();
        for(var point : pointsInBaseQuad) {
            for(var edge : point.edges) {
                graphEdges.add(new GraphEdge(new Point(point.x, point.y), edge.point, edge.weight));
            }
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        System.out.println(gson.toJson(graphEdges));

        return src;
    }
}
