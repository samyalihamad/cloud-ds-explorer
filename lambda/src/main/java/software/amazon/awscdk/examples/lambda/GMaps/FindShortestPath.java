package software.amazon.awscdk.examples.lambda.GMaps;
import DataStructures.Dijkstra;
import Factories.DIFactory;
import Interfaces.MapsRepository;
import Models.GMaps.ShortestPath;
import Models.GMaps.ShortestPathResponse;
import Models.Mappers.GraphConverter;
import Models.Point;
import Models.Vector;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.util.Pair;
import software.amazon.awscdk.examples.lambda.GatewayResponse;
import util.HttpUtil;
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

    private LambdaLogger logger;
    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        logger = context.getLogger();

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

        var response = execute(srcPoint, destPoint);
        var statusCode = response.getKey();
        var shortestPathResponse = response.getValue();

        if(shortestPathResponse.hasError()) {
            return HttpUtil.generateResponse(statusCode, shortestPathResponse.getErrorMessage());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return HttpUtil.generateResponse(statusCode, gson.toJson(shortestPathResponse.getShortestPath()));
    }

    private Pair<Integer, ShortestPathResponse> execute(Point src, Point dest) {
        try {
            logger.log("src: " + src.x + ", " + src.y);
            logger.log("dest: " + dest.x + ", " + dest.y);
            var srcPoint =  mapsRepository.getPoint(src.x, src.y);
            if(srcPoint == null) {
                return new Pair(400, new ShortestPathResponse("src point not found"));
            }
            var destPoint = mapsRepository.getPoint(dest.x, dest.y);
            if(destPoint == null) {
                return new Pair(400, new ShortestPathResponse("dest point not found"));
            }

            var srcQuadId = srcPoint.quadId;
            var destQuadId = destPoint.quadId;
            var baseQuadId = StringUtil.getCommonStartingCharacters(srcQuadId, destQuadId);

            // Query all points in the base quad
            var pointsInBaseQuad = mapsRepository.getPointsInQuad(baseQuadId);
            var vectorList = GraphConverter.toVectorList(pointsInBaseQuad);

            var dijkstra = new Dijkstra(pointsInBaseQuad);

            var shortestPath = dijkstra.findShortestPath(src, dest);

            // TODO: Refactor - assumes that response contains at least one point
            // TODO: Refactor out to a util class
            List<Vector> shortestPathVectors = new ArrayList<>();
            var prevPoint = shortestPath.get(0);
            for(var point : shortestPath) {
                shortestPathVectors.add(new Vector(prevPoint, point));
                prevPoint = point;
            }

            return new Pair(200, new ShortestPathResponse(new ShortestPath(vectorList, shortestPathVectors)));
        }
        catch(Exception e) {
            return new Pair(500, new ShortestPathResponse(e.getMessage()));
        }
    }
}