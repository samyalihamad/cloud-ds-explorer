package DataStructures;

import java.util.Comparator;
import java.util.PriorityQueue;

public class TwoHeapMedian {
    // Create a custom comparator to reverse the default order
    Comparator<Integer> maxHeapComparator = (a, b) -> b - a;
    PriorityQueue<Integer> smallMaxHeap;
    PriorityQueue<Integer> largeMinHeap;
    public TwoHeapMedian() {
        // small (max) heap   large (min) heap
        // 1 2 3 4 5 6 7 8    9 10 11 12 13 14 15
        // Create a max priority queue using the custom comparator
        smallMaxHeap = new PriorityQueue<>(maxHeapComparator);

        // Create a min priority queue
        largeMinHeap = new PriorityQueue<>();
    }

    public void addNum(int num) {
        // Default add to the small heap
        smallMaxHeap.add(num);

        // Verify that the small heap is not larger than the large heap
        if(!smallMaxHeap.isEmpty() && !largeMinHeap.isEmpty() && smallMaxHeap.peek() > largeMinHeap.peek()) {
            var smallHeapValue = smallMaxHeap.poll();
            largeMinHeap.add(smallHeapValue);
        }

        // Verify that heaps do not get larger than 1 element
        if(smallMaxHeap.size() > largeMinHeap.size() + 1) {
            var smallHeapValue = smallMaxHeap.poll();
            largeMinHeap.add(smallHeapValue);
        }
        else if(largeMinHeap.size() > smallMaxHeap.size() + 1) {
            var largeHeapValue = largeMinHeap.poll();
            smallMaxHeap.add(largeHeapValue);
        }
    }

    public double findMedian() {
        if(smallMaxHeap.size() > largeMinHeap.size()) {
            return smallMaxHeap.peek();
        }
        else if(largeMinHeap.size() > smallMaxHeap.size()) {
            return largeMinHeap.peek();
        }
        else {
            return ((double)smallMaxHeap.peek() + largeMinHeap.peek()) / 2;
        }
    }
}
