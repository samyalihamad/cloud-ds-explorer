package cloud.ds.explorer.lambda.MedianTracker;

import DataStructures.TwoHeapMedian;
import Models.TwoHeaps.TwoHeapsInput;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import software.amazon.awscdk.examples.lambda.GatewayResponse;
import util.HttpUtil;
import util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValuePostHandler implements RequestHandler<Map<String, Object>, GatewayResponse> {
	@Override
	public GatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LambdaLogger logger = context.getLogger();

		String body = (String)input.get("body");
		logger.log("Body is: "+body);

		var response = addValue(body);

		System.out.println("response: "+response);

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		return HttpUtil.generateResponse(200, gson.toJson(response));
	}

	private List<Double> addValue(String body) {
		var output = new ArrayList<Double>();
		try {
			TwoHeapsInput input = JsonUtil.parseJson(body, TwoHeapsInput.class);
			var twoHeaps = new TwoHeapMedian();

			List<List<Integer>> inputs = input.getInputArray();
			List<String> commands = input.getCommandArray();
			for(int i = 0; i < commands.size(); i++) {
				String cmd = commands.get(i);
				System.out.println("--------------------");
				System.out.println("Command: " + cmd);

				if(cmd.equals("addNum")) {
					twoHeaps.addNum(inputs.get(i).get(0));
				} else if(cmd.equals("findMedian")) {
					output.add(twoHeaps.findMedian());
				}
			}
			return output;
		} catch (Exception e) {
			return output;
		}
	}
}
