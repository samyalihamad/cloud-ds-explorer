package Models.GMaps;

import Models.Point;
import Models.Vector;
import java.util.List;

public class ShortestPathResponse {
	public List<Vector> vicinityPaths;
	public List<Vector> shortestPaths;

	public ShortestPathResponse(List<Vector> vicinityPaths, List<Vector> shortestPaths) {
		this.shortestPaths = shortestPaths;
		this.vicinityPaths = vicinityPaths;
	}
}
