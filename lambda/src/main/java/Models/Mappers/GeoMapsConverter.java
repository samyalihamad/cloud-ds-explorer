package Models.Mappers;

import Models.Point;
import Models.PointEdge;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.ScanOutcome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeoMapsConverter {
    public static List<Point> convertToGeoPointQuery(ItemCollection<QueryOutcome> dbPoints) {
        List<Point> points = new ArrayList<>();

        for (var dbPoint : dbPoints) {
            points.add(convertToGeoPoint(dbPoint));
        }

        return points;
    }

    public static List<Point> convertToGeoPointScan(ItemCollection<ScanOutcome> dbPoints) {
        List<Point> points = new ArrayList<>();

        for (var dbPoint : dbPoints) {
            points.add(convertToGeoPoint(dbPoint));
        }

        return points;
    }

    public static Point convertToGeoPoint(Item dbPoint) {
        String strPoint = dbPoint.getString("point");
        String quadId = dbPoint.getString("quadId");

        List<PointEdge> edgePoints = new ArrayList<>();
        List<Map<String, Object>> edges = dbPoint.getList("edges");
        for(var edge : edges) {
            Point point = new Point(Integer.parseInt(edge.get("point").toString().split(",")[0]),
                    Integer.parseInt(edge.get("point").toString().split(",")[1]));
            int weight = Integer.parseInt(edge.get("weight").toString());
            edgePoints.add(new PointEdge(point, weight));
        }

        Point point = new Point(Integer.parseInt(strPoint.split(",")[0]), Integer.parseInt(strPoint.split(",")[1]), quadId);
        point.edges = edgePoints;
        return point;
    }
}
