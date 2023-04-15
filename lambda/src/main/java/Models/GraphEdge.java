package Models;

import java.util.Objects;

public class GraphEdge {
    public Point a, b;
    public int weight;

    public GraphEdge(Point a, Point b, int weight) {
        this.a = a;
        this.b = b;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null || getClass() != obj.getClass())
            return false;

        return ((GraphEdge)obj).a.equals(this.a) && ((GraphEdge)obj).b.equals(this.b) ||
                ((GraphEdge)obj).b.equals(this.a) && ((GraphEdge)obj).a.equals(this.b);
    }

    @Override
    public int hashCode() {
        int hashA = Objects.hash(a, b);
        int hashB = Objects.hash(b, a);

        return Math.min(hashA, hashB);
    }
}
