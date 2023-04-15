package Models;

import java.util.Objects;

public class PointEdge {
    public Point point;
    public int weight;

    public PointEdge(Point point, int weight) {
        this.point = point;
        this.weight = weight;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null || getClass() != obj.getClass())
            return false;

        return ((PointEdge)obj).point.equals(this.point) && ((PointEdge)obj).weight == this.weight;
    }

    @Override
    public int hashCode() {
        return Objects.hash(point, weight);
    }
}
