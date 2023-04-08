package DataStructures;

import Models.QuadTree.QuadTreeNode;
import java.util.*;

public class QuadTree {
    public QuadTreeNode root;
    public Set<String> quadIds = new HashSet<>();

    public QuadTree(int x1, int y1, int x2, int y2, int depth, boolean buildAdjacencyList) {
        this.root = new QuadTreeNode(x1, y1, x2, y2, "1");
        subdivide(root, depth, buildAdjacencyList);
    }

    private void subdivide(QuadTreeNode node, int depth, boolean buildAdjacencyList) {
        if(depth == 0) {
            quadIds.add(node.id);
            return;
        }

        int midX = (node.x1 + node.x2) / 2;
        int midY = (node.y1 + node.y2) / 2;

        var quadId0 = String.format("%s,%s;%s,%s", node.x1, midY, midX, node.y2);
        node.children[0] =  new QuadTreeNode(node.x1, midY, midX, node.y2, quadId0); // Top-left

        var quadId1 = String.format("%s,%s;%s,%s", midX, midY, node.x2, node.y2);
        node.children[1] = new QuadTreeNode(midX, midY, node.x2, node.y2, quadId1); // Top-right

        var quadId2 = String.format("%s,%s;%s,%s", node.x1, node.y1, midX, midY);
        node.children[2] = new QuadTreeNode(node.x1, node.y1, midX, midY, quadId2); // Bottom-left

        var quadId3 = String.format("%s,%s;%s,%s", midX, node.y1, node.x2, midY);
        node.children[3] = new QuadTreeNode(midX, node.y1, node.x2, midY, quadId3); // Bottom-right

        for(QuadTreeNode child : node.children) {
            subdivide(child, depth - 1, buildAdjacencyList);
        }
    }

    public String findQuadId(int x, int y) {
        var curr = root;
        String id = "";
        while(curr != null) {
            int midX = (curr.x1 + curr.x2) / 2;
            int midY = (curr.y1 + curr.y2) / 2;
            id = curr.id;
            if(x < midX && y >= midY) {
                curr = curr.children[0];
            } else if(x >= midX && y >= midY) {
                curr = curr.children[1];
            } else if(x < midX && y < midY) {
                curr = curr.children[2];
            } else {
                curr = curr.children[3];
            }
        }

        return id;
    }
}
