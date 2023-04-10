package software.amazon.awscdk.examples;

import software.constructs.Construct;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.FunctionProps;
import software.amazon.awscdk.services.lambda.Runtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * CorsLambdaCrudDynamodbStack CDK example for Java!
 */
class CorsLambdaCrudDynamodbStack extends Stack {
    public CorsLambdaCrudDynamodbStack(final Construct parent, final String name) {
        super(parent, name);

        TableProps gMapsTableProps;
        Attribute gMapsPartitionKey = Attribute.builder()
                .name("point")
                .type(AttributeType.STRING)
                .build();
        Attribute gMapsSortKey = Attribute.builder()
                .name("quadId")
                .type(AttributeType.STRING)
                .build();

        gMapsTableProps = TableProps.builder()
                .tableName("GeoMaps")
                .partitionKey(gMapsPartitionKey)
                .sortKey(gMapsSortKey)
                // The default removal policy is RETAIN, which means that cdk destroy will not attempt to delete
                // the new table, and it will remain in your account until manually deleted. By setting the policy to
                // DESTROY, cdk destroy will delete the table (even if it has data in it)
                .removalPolicy(RemovalPolicy.DESTROY)
                .build();
        Table gMapsTable = new Table(this, "GeoMaps", gMapsTableProps);
//
        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME", gMapsTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY","point");


        Function segmentTreeFunction = new Function(this, "segmentTreeFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.SegmentTree"));
        Function twoHeapMedianFunction = new Function(this, "twoHeapsFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.TwoHeaps"));
        Function gMapsDataGeneratorFunction = new Function(this, "gMapsDataGeneratorFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.GMaps.GMapDataGenerator"));
        Function gMapsGetShortestPath = new Function(this, "gMapsGetShortestPath",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.GMaps.FindShortestPath"));



        gMapsTable.grantReadWriteData(gMapsDataGeneratorFunction);
        gMapsTable.grantReadWriteData(gMapsGetShortestPath);
        gMapsTable.grantReadWriteData(segmentTreeFunction);

        RestApi api = new RestApi(this, "ds-explorer",
                RestApiProps.builder().restApiName("Cloud DS Explorer").build());

        // Segment Tree
        IResource segmentTree = api.getRoot().addResource("segmentTree");
        Integration segmentTreeIntegration = new LambdaIntegration(segmentTreeFunction);
        segmentTree.addMethod("POST", segmentTreeIntegration);
        addCorsOptions(segmentTree);

        // Two Heaps
        IResource twoHeapMedian = api.getRoot().addResource("twoHeapMedian");
        Integration twoHeapMedianIntegration = new LambdaIntegration(twoHeapMedianFunction);
        twoHeapMedian.addMethod("POST", twoHeapMedianIntegration);
        addCorsOptions(twoHeapMedian);

        // GMaps
        IResource gMaps = api.getRoot().addResource("gMaps");
        // GMapsDataGenerator
        Integration gMapsIntegration = new LambdaIntegration(gMapsDataGeneratorFunction);
        gMaps.addMethod("POST", gMapsIntegration);
        addCorsOptions(gMaps);

        // GMapsGetShortestPath
        IResource gMapsGetShortestPathSrcResource = gMaps.addResource("{src}");
        IResource gMapsGetShortestPathDestResource = gMapsGetShortestPathSrcResource.addResource("{dest}");

        Integration gMapsGetShortestPathIntegration = new LambdaIntegration(gMapsGetShortestPath);

        gMapsGetShortestPathDestResource.addMethod("GET", gMapsGetShortestPathIntegration);
        addCorsOptions(gMapsGetShortestPathDestResource);
    }



    private void addCorsOptions(IResource item) {
        List<MethodResponse> methoedResponses = new ArrayList<>();

        Map<String, Boolean> responseParameters = new HashMap<>();
        responseParameters.put("method.response.header.Access-Control-Allow-Headers", Boolean.TRUE);
        responseParameters.put("method.response.header.Access-Control-Allow-Methods", Boolean.TRUE);
        responseParameters.put("method.response.header.Access-Control-Allow-Credentials", Boolean.TRUE);
        responseParameters.put("method.response.header.Access-Control-Allow-Origin", Boolean.TRUE);
        methoedResponses.add(MethodResponse.builder()
                .responseParameters(responseParameters)
                .statusCode("200")
                .build());
        MethodOptions methodOptions = MethodOptions.builder()
                .methodResponses(methoedResponses)
                .build()
                ;

        Map<String, String> requestTemplate = new HashMap<>();
        requestTemplate.put("application/json","{\"statusCode\": 200}");
        List<IntegrationResponse> integrationResponses = new ArrayList<>();

        Map<String, String> integrationResponseParameters = new HashMap<>();
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Headers","'Content-Type,X-Amz-Date,Authorization,X-Api-Key,X-Amz-Security-Token,X-Amz-User-Agent'");
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Origin","'*'");
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Credentials","'false'");
        integrationResponseParameters.put("method.response.header.Access-Control-Allow-Methods","'OPTIONS,GET,PUT,POST,DELETE'");
        integrationResponses.add(IntegrationResponse.builder()
                .responseParameters(integrationResponseParameters)
                .statusCode("200")
                .build());
        Integration methodIntegration = MockIntegration.Builder.create()
                .integrationResponses(integrationResponses)
                .passthroughBehavior(PassthroughBehavior.NEVER)
                .requestTemplates(requestTemplate)
                .build();

        item.addMethod("OPTIONS", methodIntegration, methodOptions);
    }

    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String handler) {
        return FunctionProps.builder()
                    .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
                    .handler(handler)
                    .runtime(Runtime.JAVA_11)
                    .environment(lambdaEnvMap)
                    .timeout(Duration.seconds(30))
                    .memorySize(512)
                    .build();
    }
}
