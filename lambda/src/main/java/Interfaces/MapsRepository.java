package Interfaces;

import Models.Point;
import Models.PointEdge;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface MapsRepository {
    Point getPoint(int x, int y);

    void savePoint(Point point, Set<PointEdge> edges);

    Map<Point, List<PointEdge>> getPointsInQuad(String quadId);
}
