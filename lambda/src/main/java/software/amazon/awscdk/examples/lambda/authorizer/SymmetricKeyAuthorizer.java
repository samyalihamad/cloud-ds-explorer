package software.amazon.awscdk.examples.lambda.authorizer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awscdk.examples.lambda.GatewayResponse;

import java.util.HashMap;
import java.util.Map;

public class SymmetricKeyAuthorizer implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Inside software.amazon.awscdk.examples.lambda: Authorizer "+input.getClass()+ " data:"+input);

        // Get the API key from the request
        String apiKey = (String) input.get("authorizationToken");

        // Define a hardcoded API key for this example
        String expectedApiKey = "symmetric-key-example";

        // Check if the provided API key matches the expected API key
        if(apiKey.equals(expectedApiKey)) {
            // Return an "Allow" policy for the request
            return generatePolicy("user", "Allow", input.get("methodArn").toString());
        } else {
            // Return a "Deny" policy for the request
            return generatePolicy("user", "Deny", input.get("methodArn").toString());
        }
    }

    private Map<String, Object> generatePolicy(String principalId, String effect, String resource) {
        Map<String, Object> authResponse = new HashMap<>();
        authResponse.put("principalId", principalId);
        if (effect != null && resource != null) {
            Map<String, Object> policyDocument = new HashMap<>();
            policyDocument.put("Version", "2012-10-17");
            policyDocument.put("Statement", new Object[] { new HashMap<String, Object>() {
                {
                    put("Action", "execute-api:Invoke");
                    put("Effect", effect);
                    put("Resource", resource);
                }
            } });
            authResponse.put("policyDocument", policyDocument);
        }
        return authResponse;
    }
}
