package cloud.ds.explorer.lambda;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GatewayResponse {

    private final String body;
    private final Map<String, String> headers;

    private final int statusCode;

    public GatewayResponse(final String body, final Map<String, String> headers, final int statusCode) {
        this.statusCode = statusCode;
        this.body = body;
        // When configuring the proxy integration on the API Gateway,
        // the Lambda function needs to return a response in a specific format.
        headers.put("Access-Control-Allow-Origin", "*" );
        this.headers = Collections.unmodifiableMap(new HashMap<>(headers));
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
