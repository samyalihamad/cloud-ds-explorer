package Models.SegmentTree;

public class QueryCommand implements ICommand {
    private String command;
    private int left;
    private int right;

    public QueryCommand(int left, int right) {
        this.command = "sumRange";
        this.left = left;
        this.right = right;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }
}
