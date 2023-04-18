package software.amazon.awscdk.examples;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

public class CloudExplorerSharedResourcesStack extends Stack {
	private final Vpc vpc;
	public CloudExplorerSharedResourcesStack(final Construct parent, final String name) {
		super(parent, name);

		// Create a VPC
		Vpc vpc = Vpc.Builder.create(this, "CloudExplorerVPC")
			.maxAzs(2)
			.build();

		this.vpc = vpc;
	}

	public Vpc getVpc() {
		return this.vpc;
	}
}
