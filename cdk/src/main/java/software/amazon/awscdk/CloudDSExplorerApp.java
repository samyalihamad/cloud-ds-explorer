package software.amazon.awscdk;

import software.amazon.awscdk.services.ec2.Vpc;

public class CloudDSExplorerApp {
    public static void main(final String[] args) {
        App app = new App();

        var sharedStack = new CloudDSExplorerSharedResourcesStack(app, "cdk-cloud-ds-explorer-shared-resources");
        Vpc vpc = sharedStack.getVpc();
        var elasticacheStack = new CloudDSExplorerElasticacheStack(app, "cdk-cloud-ds-explorer-elasticache", vpc);
        var lambdaStack = new CloudDSExplorerApiGatewayLambdaStack(app, "cdk-cloud-ds-explorer-api-gateway-lambda", vpc, StackProps.builder().build());

        elasticacheStack.addDependency(sharedStack);
        lambdaStack.addDependency(elasticacheStack);

        app.synth();
    }
}
