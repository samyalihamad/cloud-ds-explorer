package software.amazon.awscdk.examples;

import software.amazon.awscdk.*;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.IResource;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.dynamodb.Attribute;
import software.amazon.awscdk.services.dynamodb.AttributeType;
import software.amazon.awscdk.services.dynamodb.Table;
import software.amazon.awscdk.services.dynamodb.TableProps;
import software.amazon.awscdk.services.lambda.*;

import java.util.*;

/**
 * CorsLambdaCrudDynamodbStack CDK example for Java!
 */
class CorsLambdaCrudDynamodbStack extends Stack {
    public CorsLambdaCrudDynamodbStack(final Construct parent, final String name, final Vpc vpc, final StackProps props) {
        super(parent, name);

        // Import the Redis endpoint using its export name
        String redisEndpoint = Fn.importValue("RedisEndpointExport").toString();


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

        Map<String, String> lambdaEnvMap = new HashMap<>();
        lambdaEnvMap.put("TABLE_NAME", gMapsTable.getTableName());
        lambdaEnvMap.put("PRIMARY_KEY","point");
        lambdaEnvMap.put("REDIS_ENDPOINT", redisEndpoint);

        var sg = SecurityGroup.fromSecurityGroupId(this, "ImportedRedisSecurityGroup", vpc.getVpcDefaultSecurityGroup());

        Function segmentTreeFunction = new Function(this, "segmentTreeFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.SegmentTree", vpc, sg));

        Function gMapsDataGeneratorFunction = new Function(this, "gMapsDataGeneratorFunction",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.GMaps.GMapDataGenerator", vpc, sg));
        Function gMapsGetShortestPath = new Function(this, "gMapsGetShortestPath",
                getLambdaFunctionProps(lambdaEnvMap, "software.amazon.awscdk.examples.lambda.GMaps.FindShortestPath", vpc, sg));

        gMapsTable.grantReadWriteData(gMapsDataGeneratorFunction);
        gMapsTable.grantReadWriteData(gMapsGetShortestPath);
        gMapsTable.grantReadWriteData(segmentTreeFunction);

        // Create the API Gateway
        RestApi api = new RestApi(this, "ds-explorer",
                RestApiProps.builder().restApiName("Cloud DS Explorer").build());

        // Create an API Key
        ApiKey apiKey = new ApiKey(this, "freeTier",
                ApiKeyProps.builder().apiKeyName("freeTier").build());


        // Create a Usage Plan and associate it with the API Key
        UsagePlan usagePlan = api.addUsagePlan("dsExplorerUsagePlan", UsagePlanProps.builder()
            .name("freeTierPlan")
            .throttle(ThrottleSettings.builder()
                .rateLimit(10)
                .burstLimit(2)
                .build())
            .apiStages(List.of(UsagePlanPerApiStage.builder()
                .api(api)
                .stage(api.getDeploymentStage())
                .build()))
            .build()
        );

        // Attach the Usage Plan to the API Stage
        CfnUsagePlanKey.Builder.create(this, "UsagePlanKey")
                .keyId(apiKey.getKeyId())
                .keyType("API_KEY")
                .usagePlanId(usagePlan.getUsagePlanId())
                .build();


        // Segment Tree
        IResource segmentTree = api.getRoot().addResource("segmentTree");
        Integration segmentTreeIntegration = new LambdaIntegration(segmentTreeFunction);
        addMethod(segmentTree, "POST", segmentTreeIntegration);
        addCorsOptions(segmentTree);

        // Two Heaps
        IResource medianTracker = api.getRoot().addResource("medianTracker");
        addCorsOptions(medianTracker);
        // Two Heaps Add Value
        Function twoHeapMedianAddValueFunction = new Function(this, "twoHeapMedianAddValueFunction",
                getLambdaFunctionProps(lambdaEnvMap, "cloud.ds.explorer.lambda.MedianTracker.ValuePostHandler", vpc, sg));

        Integration twoHeapMedianIntegration = new LambdaIntegration(twoHeapMedianAddValueFunction);
        addMethod(medianTracker, "POST", twoHeapMedianIntegration);

        // Two Heaps Get Median
        Function twoHeapMedianGetMedianFunction = new Function(this, "twoHeapMedianGetMedianFunction",
                getLambdaFunctionProps(lambdaEnvMap, "cloud.ds.explorer.lambda.MedianTracker.ValueGetHandler", vpc, sg));

        Integration twoHeapMedianGetMedianIntegration = new LambdaIntegration(twoHeapMedianGetMedianFunction);
        addMethod(medianTracker, "GET", twoHeapMedianGetMedianIntegration);

        // GMaps
        IResource gMaps = api.getRoot().addResource("gMaps");
        // GMapsDataGenerator
        Integration gMapsIntegration = new LambdaIntegration(gMapsDataGeneratorFunction);
        addMethod(gMaps, "POST", gMapsIntegration);
        addCorsOptions(gMaps);

        // GMapsGetShortestPath
        IResource gMapsGetShortestPathSrcResource = gMaps.addResource("{src}");
        IResource gMapsGetShortestPathDestResource = gMapsGetShortestPathSrcResource.addResource("{dest}");
        Integration gMapsGetShortestPathIntegration = new LambdaIntegration(gMapsGetShortestPath);

        addMethod(gMapsGetShortestPathDestResource, "GET", gMapsGetShortestPathIntegration);
        addCorsOptions(gMapsGetShortestPathDestResource);
    }

    private void addMethod(IResource resource, String method, Integration integration) {
        MethodOptions methodOptions = MethodOptions.builder()
                .apiKeyRequired(true)
                .build();

        resource.addMethod(method, integration, methodOptions);
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

    private FunctionProps getLambdaFunctionProps(Map<String, String> lambdaEnvMap, String handler, Vpc vpc, ISecurityGroup sg) {

        return FunctionProps.builder()
            .code(Code.fromAsset("./asset/lambda-1.0.0-jar-with-dependencies.jar"))
            .handler(handler)
            .vpc(vpc)
            .vpcSubnets(SubnetSelection.builder()
                .subnetType(SubnetType.PRIVATE_WITH_EGRESS)
                .build())
            .securityGroups(Collections.singletonList(sg))
            .runtime(Runtime.JAVA_11)
            .environment(lambdaEnvMap)
            .timeout(Duration.seconds(30))
            .memorySize(512)
            .build();
    }
}
