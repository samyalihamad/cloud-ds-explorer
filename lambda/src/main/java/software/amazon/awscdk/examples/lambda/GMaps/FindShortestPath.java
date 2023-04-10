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

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*" );
        return new GatewayResponse(output, headers, 200);
    }

    private String execute(Point src, Point dest) {

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
        var output = gson.toJson(graphEdges);
        System.out.println(output);

        return output;
    }
}
