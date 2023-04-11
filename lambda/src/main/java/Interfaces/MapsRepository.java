package Interfaces;

import Models.Point;
import Models.PointEdge;

import java.util.List;
import java.util.Map;

public interface MapsRepository {
    List<Point> getPoint(int x, int y);

    void savePoint(Point point, List<PointEdge> edges);

    Map<Point, List<PointEdge>> getPointsInQuad(String quadId);
}
