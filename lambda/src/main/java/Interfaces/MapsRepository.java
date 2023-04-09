package Interfaces;

import Models.Point;
import Models.PointEdge;

import java.util.List;

public interface MapsRepository {
    List<Point> getPoint(int x, int y);

    void savePoint(Point point, List<PointEdge> edges);

    List<Point> getPointsInQuad(String quadId);
}
