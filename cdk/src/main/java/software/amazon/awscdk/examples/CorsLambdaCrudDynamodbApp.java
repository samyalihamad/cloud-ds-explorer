package software.amazon.awscdk.examples;

import software.amazon.awscdk.App;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;

public class CorsLambdaCrudDynamodbApp {
    public static void main(final String[] args) {
        App app = new App();

        var sharedStack = new CloudExplorerSharedResourcesStack(app, "cdk-cloud-explorer-shared-resources");
        Vpc vpc = sharedStack.getVpc();
        var elasticacheStack = new CloudExplorerElasticacheStack(app, "cdk-cloud-explorer-elasticache", vpc);
        var lambdaStack = new CorsLambdaCrudDynamodbStack(app, "cdk-cors-lambda-crud-dynamodb-example", vpc, StackProps.builder().build());

        elasticacheStack.addDependency(sharedStack);
        lambdaStack.addDependency(elasticacheStack);

        app.synth();
    }
}
