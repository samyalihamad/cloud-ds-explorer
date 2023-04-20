package cloud.ds.explorer.lambda;

import DataStructures.NumArray;
import Models.SegmentTree.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SegmentTree implements RequestHandler<Map<String,Object>, GatewayResponse>{
    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside software.amazon.awscdk.lambda: getOneItem "+input.getClass()+ " data:"+input);

        String body = (String)input.get("body");
        logger.log("Body is:"+body);

        var output = execute(body);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new GatewayResponse(output.toString(), headers, 200);
    }

    private List<Integer> execute(String body) {
        var output = new ArrayList<Integer>();
        try{
            SegmentInput input = JsonUtil.parseJson(body, SegmentInput.class);
            var numArray = new NumArray(input.getInitialInput().stream().mapToInt(i->i).toArray());

            List<ICommand> commandList = new ArrayList<ICommand>();
            var commands = input.getCommands();
            for(int i = 0; i < commands.size(); i++) {
                if(commands.get(i).equals("update"))
                    commandList.add(new UpdateCommand(input.getInputs().get(i).get(0), input.getInputs().get(i).get(1)));
                else if(commands.get(i).equals("sumRange"))
                    commandList.add(new QueryCommand(input.getInputs().get(i).get(0), input.getInputs().get(i).get(1)));
                else
                    System.out.println("Invalid command");
            }

            for(var command : commandList) {
                if(command.getCommand().equals("update")) {
                    UpdateCommand updateCommand = (UpdateCommand) command;
                    numArray.update(updateCommand.getIndex(), updateCommand.getValue());
                }
                else if(command.getCommand().equals("sumRange")) {
                    QueryCommand queryCommand = (QueryCommand) command;
                    var sum = numArray.sumRange(queryCommand.getLeft(), queryCommand.getRight());
                    System.out.println("Sum: " + sum);
                    output.add(sum);
                }
                else {
                    System.out.println("Invalid command");
                }
            }

            return output;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return output;
        }
    }
}
