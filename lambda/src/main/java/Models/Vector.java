package Models;

import java.util.Objects;

public class Vector {
	public Point a;
	public Point b;

	public Vector(int ax, int ay, int bx, int by) {
		this.a = new Point(ax, ay);
		this.b = new Point(bx, by);
	}

	public Vector (Point a, Point b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals (Object obj) {
		if(this == obj)
			return true;

		if(obj == null || getClass() != obj.getClass())
			return false;

		var vector = (Vector)obj;

		return (vector.a.equals(this.a) && vector.b.equals(this.b)) ||
						(vector.b.equals(this.a) && vector.a.equals(this.b));
	}

	@Override
	public int hashCode() {
		int hashA = Objects.hash(a, b);
		int hashB = Objects.hash(b, a);

		return Math.min(hashA, hashB);
	}
}
