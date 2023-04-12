package DataStructures;

import Models.Point;
import Models.PointEdge;
import javafx.util.Pair;

import java.util.*;

public class Dijkstra {

    private Map<Point, List<PointEdge>> points;
    public Dijkstra(Map<Point, List<PointEdge>> points) {
        this.points = points;
    }

    public List<Point> findShortestPath(Point start, Point end) {
        List<Point> shortestPath = new ArrayList<>();

        // Shortest Path Lookup Table
        // The benefit of this format is we solve the shortest distance and shortest path at the same time
        HashMap<Point, Pair<Integer, Point>> shortestTableLookup = new HashMap<>();

        PriorityQueue<Pair <PointEdge, Point>> nextClosestNodes = new PriorityQueue<>((a, b) -> a.getKey().weight - b.getKey().weight);
        nextClosestNodes.add(new Pair(new PointEdge(start, 0), null));
        while(!nextClosestNodes.isEmpty()) {
            var currentNode = nextClosestNodes.poll();

            if(shortestTableLookup.containsKey(currentNode.getKey().point)) {
                continue;
            }

            shortestTableLookup.putIfAbsent(currentNode.getKey().point, new Pair(currentNode.getKey().weight, currentNode.getValue()));

            var currentNodeNeighbors = points.get(currentNode.getKey().point);
            if(currentNodeNeighbors == null) {
                continue;
            }

            for(var neighbor : currentNodeNeighbors) {
                if(shortestTableLookup.containsKey(neighbor.point))
                    continue;

                var newDistance = currentNode.getKey().weight + neighbor.weight;

                // Add the neighbor to the next closest nodes if it has not been visited
                nextClosestNodes.add(new Pair(new PointEdge(neighbor.point, newDistance), currentNode.getKey().point));
            }
        }

        // Build the shortest path
        var current = end;
        while(current != null) {
            shortestPath.add(current);
            var temp = shortestTableLookup.get(current);
            current = temp.getValue();
        }

        Collections.reverse(shortestPath);

        return shortestPath;
    }


    public List<Point> findShortestPathUnoptimized(Point start, Point end) {
        List<Point> shortestPath = new ArrayList<>();

        // Shortest Path Lookup Table
        HashMap<Point, Pair<Integer, Point>> shortestTableLookup = new HashMap<>();

        // Set the start point to have a distance of 0
        shortestTableLookup.put(start, new Pair(0, null));

        for(var point : points.entrySet()) {
            shortestTableLookup.putIfAbsent(point.getKey(), new Pair(Integer.MAX_VALUE, null));
        }

        Set<Point> visited = new HashSet<>();
        var currentNode = start;

        PriorityQueue<PointEdge> nextClosestNodes = new PriorityQueue<>((a, b) -> a.weight - b.weight);
        while(!visited.contains(end)) {
            visited.add(currentNode);

            var currentNodeNeighbors = points.get(currentNode);

            if(currentNodeNeighbors == null) {
                continue;
            }

            for(var neighbor : currentNodeNeighbors) {
                if(visited.contains(neighbor.point) || !shortestTableLookup.containsKey(neighbor.point))
                    continue;

                var currentDistance = shortestTableLookup.get(currentNode).getKey();
                var newDistance = currentDistance + neighbor.weight;

                if(newDistance < shortestTableLookup.get(neighbor.point).getKey()) {
                    shortestTableLookup.put(neighbor.point, new Pair(newDistance, currentNode));
                }

                // Add the neighbor to the next closest nodes if it has not been visited
                nextClosestNodes.add(new PointEdge(neighbor.point, shortestTableLookup.get(neighbor.point).getKey()));
            }

            // Get the next closest node to visit
            currentNode = nextClosestNodes.poll().point;
        }

        // Build the shortest path
        var current = end;
        while(current != null) {
            shortestPath.add(current);
            current = shortestTableLookup.get(current).getValue();
        }

        Collections.reverse(shortestPath);

        return shortestPath;
    }

    public Integer findShortestDistance(Point start, Point end) {
        // For Dijktra's algorithm, we need to start at a point and
        // expand outwards in a expanding frontier approach until we
        // reach the end point. We will use a priority queue to keep
        // track of the frontier and the points we have already visited.

        var minHeapQueue = new PriorityQueue<PointEdge>((a, b) -> a.weight - b.weight);

        HashMap<Point, Integer> shortest = new HashMap<>();
        minHeapQueue.add(new PointEdge(start, 0));

        while(!minHeapQueue.isEmpty()) {
            var current = minHeapQueue.poll();

            if(shortest.containsKey(current.point)) {
                continue;
            }

            shortest.put(current.point, current.weight);
            var nextEdges = points.get(current.point);
            if(nextEdges == null) {
                continue;
            }
            for(var edge : nextEdges) {
                if(!shortest.containsKey(edge.point)) {
                    minHeapQueue.add(new PointEdge(edge.point, current.weight + edge.weight));
                }
            }
        }

        return shortest.get(start);
    }
}
