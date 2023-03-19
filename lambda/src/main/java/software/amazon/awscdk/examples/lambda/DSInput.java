package software.amazon.awscdk.examples.lambda;
import java.util.List;

public class DSInput {
    private List<Integer> inputArray;
    private List<String> commandArray;

    // Getters and setters

    public List<Integer> getInputArray() {
        return inputArray;
    }

    public void setInputArray(List<Integer> inputArray) {
        this.inputArray = inputArray;
    }

    public List<String> getCommandArray() {
        return commandArray;
    }

    public void setCommandArray(List<String> commandArray) {
        this.commandArray = commandArray;
    }
}
