package Models.GMaps;

public class ShortestPathResponse {
	private ShortestPath shortestPath;
	private String errorMessage;

	public ShortestPathResponse(ShortestPath shortestPath) {
		this.shortestPath = shortestPath;
	}

	public ShortestPathResponse(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public boolean hasError() {
		return errorMessage != null;
	}

	public ShortestPath getShortestPath() {
		return shortestPath;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
}
