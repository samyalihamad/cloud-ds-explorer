package Models;

public class Point {
    public int x, y;
    public String quadId;

    public Point(int x, int y, String quadId) {
        this.x = x;
        this.y = y;
        this.quadId = quadId;
    }

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj == null || getClass() != obj.getClass())
            return false;

        Point point = (Point)obj;
        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        // Use a prime number (e.g., 31) to reduce the likelihood of hash collisions
        int prime = 31;
        int result = 1;

        // Calculate the hash code using the prime number and the x and y coordinates
        result = prime * result + x;
        result = prime * result + y;

        return result;
    }
}
