package DataStructures;

import Models.QuadTree.QuadTreeNode;

public class QuadTree {
    QuadTreeNode root;

    public QuadTree(int x1, int y1, int x2, int y2, int depth) {
        this.root = new QuadTreeNode(x1, y1, x2, y2);
        subdivide(root, depth);
    }

    private void subdivide(QuadTreeNode node, int depth) {
        if(depth == 0) return;

        int midX = (node.x1 + node.x2) / 2;
        int midY = (node.y1 + node.y2) / 2;

        node.children[0] = new QuadTreeNode(node.x1, midY, midX, node.y2); // Top-left
        node.children[1] = new QuadTreeNode(midX, midY, node.x2, node.y2); // Top-right
        node.children[2] = new QuadTreeNode(node.x1, node.y1, midX, midY); // Bottom-left
        node.children[3] = new QuadTreeNode(midX, node.y1, node.x2, midY); // Bottom-right

        for(QuadTreeNode child : node.children) {
            subdivide(child, depth - 1);
        }
    }
}
