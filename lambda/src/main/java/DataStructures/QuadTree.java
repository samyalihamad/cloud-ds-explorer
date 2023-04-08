package DataStructures;

import Models.QuadTree.QuadTreeNode;
import java.util.*;

public class QuadTree {
    public QuadTreeNode root;
    public Set<String> quadRanges = new HashSet<>();

    public QuadTree(int x1, int y1, int x2, int y2, int depth) {
        var rootRange = String.format("%s,%s;%s,%s", x1, y1, x2, y2);
        this.root = new QuadTreeNode(x1, y1, x2, y2, "", rootRange);
        subdivide(root, depth);
    }

    private void subdivide(QuadTreeNode node, int depth) {
        if(depth == 0) {
            quadRanges.add(node.range);
            return;
        }

        int midX = (node.x1 + node.x2) / 2;
        int midY = (node.y1 + node.y2) / 2;

        var quadRange0 = String.format("%s,%s;%s,%s", node.x1, midY, midX, node.y2);
        var quadId0 = node.id + "0";
        node.children[0] =  new QuadTreeNode(node.x1, midY, midX, node.y2, quadId0, quadRange0); // Top-left

        var quadRange1 = String.format("%s,%s;%s,%s", midX, midY, node.x2, node.y2);
        var quadId1 = node.id + "1";
        node.children[1] = new QuadTreeNode(midX, midY, node.x2, node.y2, quadId1, quadRange1); // Top-right

        var quadRange2 = String.format("%s,%s;%s,%s", node.x1, node.y1, midX, midY);
        var quadId2 = node.id + "2";
        node.children[2] = new QuadTreeNode(node.x1, node.y1, midX, midY, quadId2, quadRange2); // Bottom-left

        var quadRange3 = String.format("%s,%s;%s,%s", midX, node.y1, node.x2, midY);
        var quadId3 = node.id + "3";
        node.children[3] = new QuadTreeNode(midX, node.y1, node.x2, midY, quadId3, quadRange3); // Bottom-right

        for(QuadTreeNode child : node.children) {
            subdivide(child, depth - 1);
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
