package DataStructures;

public class BinarySearch {

	private int[] dataset;

	public BinarySearch(int[] dataset) {
		this.dataset = dataset;
	}

	// returns index of found target
	public int search(int target) {
		int left = 0;
		int right = dataset.length - 1;
		int pivot = 0;

		while(left <= right) {

			pivot = (left + right) >>> 1;
			if(dataset[pivot] == target)
				return pivot;

			if(dataset[pivot] > target)
				right = pivot - 1;
			else
				left = pivot + 1;
		}

		return pivot;
	}
}
