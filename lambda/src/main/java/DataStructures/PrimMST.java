package DataStructures;

import Models.GraphEdge;
import Models.Point;
import com.sun.javafx.geom.Edge;
import javafx.util.Pair;

import java.util.*;

public class PrimMST {
    private List<Point> points;
    List<GraphEdge> edges = new ArrayList<>();
    public PrimMST(List<Point> points) {
        this.points = points;
        getEdges();
    }

    private void getEdges() {
        System.out.println("--------------------");
        System.out.println("Points: " + points.size());
        for(var point : points) {
            System.out.println("x: " + point.x + " y: " + point.y);
        }
        for(var point : points) {
            for (var otherPoint : points) {
                if (point != otherPoint) {
                    int xWeight = Math.abs(point.x - otherPoint.x);
                    int yWeight = Math.abs(point.y - otherPoint.y);
                    int weight = xWeight + yWeight;

                    GraphEdge edge = new GraphEdge(point, otherPoint, weight);
                    edges.add(edge);
                }
            }
        }
        System.out.println("Edges: " + edges.size());
//        for(int i = 0; i < points.length; i++) {
//            for(int j = 0; j < points.length; j++) {
//                if(i != j) {
//                    int xWeight = Math.abs(points[i][0] - points[j][0]);
//                    int yWeight = Math.abs(points[i][1] - points[j][1]);
//                    int weight = xWeight + yWeight;
//
//                    Point a = new Point(points[i][0], points[i][1]);
//                    Point b = new Point(points[j][0], points[j][1]);
//                    GraphEdge edge = new GraphEdge(a, b, weight);
//                    edges.add(edge);
//                }
//            }
//        }
    }

    public List<GraphEdge> getMST() {
        int n = points.size();

        // Build Adjacency List
        Map<Point, ArrayList<Pair<Integer, Point>>> adjacencyList = new HashMap<>();
        for(var point : points) {
            adjacencyList.put(point, new ArrayList<>());
        }

        for(GraphEdge edge : edges) {
            var a = adjacencyList.get(edge.a);
            a.add((new Pair(edge.weight, edge.b)));

            var b = adjacencyList.get(edge.b);
            b.add(new Pair(edge.weight, edge.a));
        }


        PriorityQueue<GraphEdge> pq = new PriorityQueue<>((a, b) -> a.weight - b.weight);
        Set<Point> visited = new HashSet<>();

        // Grab the first Point and add it to the Min Heap Queue
        var start = edges.get(0).a;

        // Add first point to the queue.
        for(var neighbor : adjacencyList.get(start)) {
            pq.add(new GraphEdge(start, neighbor.getValue(), neighbor.getKey()));
        }

        int mstCost = 0;
        List<GraphEdge> mst = new ArrayList<>();
        // This is our starting point and it doesn't cost us
        // anything to visit it.
        visited.add(start);

        while(visited.size() < n) {
            // Poll the next shortest path
            GraphEdge currEdge = pq.poll();

            Point pointB = currEdge.b;
            int weight = currEdge.weight;

            // Point B is what we are processing. Do we want to
            // officially travel to point B because it is the next
            // shortest path. If we accept pointB then pointA just
            // informs us of where we came from to reach pointB
            if(visited.contains(pointB))
                continue;

            visited.add(pointB);
            mst.add(currEdge);
            mstCost = mstCost + weight;

            var pointBAdjList = adjacencyList.get(pointB);
            for(var neighbor : pointBAdjList) {
                var neighborPoint = neighbor.getValue();
                var neighborWeight = neighbor.getKey();
                if(!visited.contains(neighborPoint))
                    pq.add(new GraphEdge(pointB, neighborPoint, neighborWeight));
            }
        }

        for(var edge : mst) {
            System.out.println("(" + edge.a.x + ", " + edge.a.y + ") --> (" + edge.b.x + ", " + edge.b.y + ") weight: " + edge.weight);
        }

        return mst;
    }
}
