package DataStructures;

import Repository.RedisConnection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;
import util.SerializationUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class TwoHeapMedian {
    // Create a custom comparator to reverse the default order
    PriorityQueue<Integer> smallMaxHeap;
    PriorityQueue<Integer> largeMinHeap;
    Jedis jedis;
    public TwoHeapMedian(Jedis jedis) {
        this.jedis = jedis;
        // small (max) heap   large (min) heap
        // 1 2 3 4 5 6 7 8    9 10 11 12 13 14 15
        // Create a max priority queue using the custom comparator
        smallMaxHeap = new PriorityQueue<>(Collections.reverseOrder());

        // Create a min priority queue
        largeMinHeap = new PriorityQueue<>();

        try {
            var smMaxHeap = getSmallMaxHeap();
            if(smMaxHeap != null)
                smallMaxHeap = smMaxHeap;

            var lgMinHeap = getLargeMinHeap();
            if(lgMinHeap != null)
                largeMinHeap = lgMinHeap;

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addNum(int num) {
        // Default add to the small heap
        smallMaxHeap.add(num);

        // If the small heap max value is greater than the large heap min value, swap
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

        //TODO: Find a better way to manage the redis memory
        // Keep in mind that storing complex data structures like min heaps can have
        // performance implications due to the serialization and deserialization process.
        // If possible, consider using Redis data structures like Sorted Sets or Lists,
        // which can provide better performance and easier data manipulation. However,
        // if the min heap behavior is essential to your use case, storing it as a
        // serialized object is the most straightforward approach.

        saveHeap(smallMaxHeap, "smallMaxHeap");

        saveHeap(largeMinHeap, "largeMinHeap");
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

    private PriorityQueue<Integer> getSmallMaxHeap() throws ClassNotFoundException, IOException {
        try(Jedis jedis = RedisConnection.getJedisFromPool()) {
            byte[] minHeapKey = "smallMaxHeap".getBytes();
            var heapData = jedis.get(minHeapKey);
            if(heapData == null)
                return null;
            var list = (List<Integer>) SerializationUtils.deserialize(heapData);
            var heap = new PriorityQueue<Integer>(Collections.reverseOrder());
            heap.addAll(list);
            return heap;
        }
        catch (IOException e) {
            System.err.println("Error serializing data: " + e.getMessage());
            throw e;
        }
        catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            throw e;
        }
    }

    private PriorityQueue<Integer> getLargeMinHeap() throws ClassNotFoundException, IOException {
        try(Jedis jedis = RedisConnection.getJedisFromPool()) {
            byte[] minHeapKey = "largeMinHeap".getBytes();
            var heapData = jedis.get(minHeapKey);
            if(heapData == null)
                return null;
            var list = (List<Integer>) SerializationUtils.deserialize(heapData);
            var heap = new PriorityQueue<>(list);
            return heap;
        }
        catch (IOException e) {
            System.err.println("Error serializing data: " + e.getMessage());
            throw e;
        }
        catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            throw e;
        }
    }

    private boolean saveHeap(PriorityQueue<Integer> heap, String heapKey) {
        try(Jedis jedis = RedisConnection.getJedisFromPool()) {
            var list = heap.stream().collect(Collectors.toList());
            byte[] minHeapKey = heapKey.getBytes();
            byte[] minHeapData = SerializationUtils.serialize(list);
            jedis.set(minHeapKey, minHeapData);
            return true;
        }
        catch (IOException e) {
            System.err.println("Error serializing data: " + e.getMessage());
            return false;
        }
        catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            return false;
        }
    }
}
