package Models.Mappers;

import Models.Point;
import Models.PointEdge;
import Models.Vector;

import java.util.*;
import java.util.stream.Collectors;

public class GraphConverter {

	public static List<Vector> toVectorList(Map<Point, List<PointEdge>> adjList) {
		Set<Vector> vectorList = new HashSet<>();

		for(var point : adjList.entrySet()) {
			var pointX = new Point(point.getKey().x, point.getKey().y);
			for(var pointEdgeY : point.getValue()) {
				var vector = new Vector(pointX, pointEdgeY.point);
				if(!vectorList.contains(vector))
					vectorList.add(vector);
			}
		}

		return vectorList.stream().collect(Collectors.toList());
	}
}
