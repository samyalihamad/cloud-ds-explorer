package Models.SegmentTree;

public class UpdateCommand implements ICommand {
    private String command;
    private int index;
    private int value;

    public UpdateCommand(int index, int value) {
        this.command = "update";
        this.index = index;
        this.value = value;
    }

    @Override
    public String getCommand() {
        return command;
    }

    public int getIndex() {
        return index;
    }

    public int getValue() {
        return value;
    }
}
