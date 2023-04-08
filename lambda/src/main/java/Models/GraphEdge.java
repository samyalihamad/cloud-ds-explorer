package Models;

public class GraphEdge {
    public Point a, b;
    public int weight;

    public GraphEdge(Point a, Point b, int weight) {
        this.a = a;
        this.b = b;
        this.weight = weight;
    }
}
