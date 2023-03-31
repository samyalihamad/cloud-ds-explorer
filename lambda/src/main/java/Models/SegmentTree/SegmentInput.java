package Models.SegmentTree;
import java.util.List;

public class SegmentInput {
    private List<Integer> initialInput;
    private List<String> commands;
    private List<List<Integer>> inputs;

    // Getters and setters

    public List<Integer> getInitialInput() {
        return initialInput;
    }

    public void setInitialInput(List<Integer> initialInput) {
        this.initialInput = initialInput;
    }

    public List<List<Integer>> getInputs() {
        return inputs;
    }

    public void setInputs(List<List<Integer>> inputs) {
        this.inputs = inputs;
    }

    public List<String> getCommands() {
        return commands;
    }

    public void setCommands(List<String> commands) {
        this.commands = commands;
    }
}


