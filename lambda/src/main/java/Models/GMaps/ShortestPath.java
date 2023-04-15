package Models.GMaps;

import Models.Vector;
import java.util.List;

public class ShortestPath {
	public List<Vector> vicinityPaths;
	public List<Vector> shortestPaths;

	public ShortestPath(List<Vector> vicinityPaths, List<Vector> shortestPaths) {
		this.shortestPaths = shortestPaths;
		this.vicinityPaths = vicinityPaths;
	}
}
