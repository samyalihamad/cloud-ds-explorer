package Models.QuadTree;

public class QuadTreeNode {
    public int x1, y1, x2, y2;
    public QuadTreeNode[] children;

    public String id;
    public String range;

    public QuadTreeNode(int x1, int y1, int x2, int y2, String id, String range) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.id = id;
        this.range = range;
        this.children = new QuadTreeNode[4];
    }
}
