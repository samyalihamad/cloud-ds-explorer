package software.amazon.awscdk.examples.lambda;

import DataStructures.NumArray;
import Models.SegmentTree.Command;
import Models.SegmentTree.SegmentInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateItem implements RequestHandler<Map<String,Object>, GatewayResponse>{
    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside software.amazon.awscdk.examples.lambda: getOneItem "+input.getClass()+ " data:"+input);


        String body = (String)input.get("body");
        logger.log("Body is:"+body);


        var output = createItem(body);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new GatewayResponse(output.toString(), headers, 200);
    }

    private List<String> createItem(String body) {
        var output = new ArrayList<String>();
        try{
            JsonParser parser =  new JsonParser();
            JsonElement element = parser.parse(body);
            JsonObject jsonObject = element.getAsJsonObject();

            // Convert the JSON object to a Java object using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            SegmentInput input = objectMapper.readValue(jsonObject.toString(), SegmentInput.class);

            System.out.println("Input Array: " + input.getInputArray());
            List<Command> commands = input.getCommandArray();
            System.out.println("Command Array: " + input.getCommandArray());

            var numArray = new NumArray(input.getInputArray().stream().mapToInt(i->i).toArray());

            for(Command cmd : commands) {
                System.out.println("--------------------");
                System.out.println("Command: " + cmd.getCommand());
                System.out.println("Left: " + cmd.getLeft());
                System.out.println("Right: " + cmd.getRight());

                if(cmd.getCommand().equals("update")) {
                    numArray.update(cmd.getLeft(), cmd.getRight());
                } else if(cmd.getCommand().equals("sumRange")) {
                    var sum = "Sum: " + numArray.sumRange(cmd.getLeft(), cmd.getRight());
                    System.out.println("Sum: " + sum);
                    output.add(sum);
                }
            }

            return output;
        } catch (IOException e) {
            output.add(e.getMessage());
            return output;
        }
    }
}
