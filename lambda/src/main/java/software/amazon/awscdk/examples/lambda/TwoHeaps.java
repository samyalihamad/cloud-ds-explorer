package software.amazon.awscdk.examples.lambda;

import DataStructures.NumArray;
import DataStructures.TwoHeapMedian;
import Models.SegmentTree.SegmentInput;
import Models.TwoHeaps.TwoHeapsInput;
import Repository.RedisConnection;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.lettuce.core.api.sync.RedisCommands;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;
import util.JsonUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TwoHeaps implements RequestHandler<Map<String,Object>, GatewayResponse>{
    @Override
    public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();

        String body = (String)input.get("body");
        logger.log("Body is:"+body);


//        try(JedisPooled jedis = RedisConnection.getJediClient())
//        {
//            String agent = jedis.get("007");
//            System.out.println("Agent: " + agent);
//            if(agent == null)
//                jedis.set("007", "James Bond");
//
//            System.out.println(agent);
//        }
//
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        return new GatewayResponse("success", headers, 200);
    }

//    private List<Double> execute(String body) {
//        var output = new ArrayList<Double>();
//        try{
//            TwoHeapsInput input = JsonUtil.parseJson(body, TwoHeapsInput.class);
//
//            var twoHeaps = new TwoHeapMedian();
//
//            List<List<Integer>> inputs = input.getInputArray();
//            List<String> commands = input.getCommandArray();
//            for(int i = 0; i < commands.size(); i++) {
//                String cmd = commands.get(i);
//                System.out.println("--------------------");
//                System.out.println("Command: " + cmd);
//
//                if(cmd.equals("addNum")) {
//                    twoHeaps.addNum(inputs.get(i).get(0));
//                } else if(cmd.equals("findMedian")) {
//                    output.add(twoHeaps.findMedian());
//                }
//            }
//            return output;
//        } catch (Exception e) {
//            return output;
//        }
//    }
}
