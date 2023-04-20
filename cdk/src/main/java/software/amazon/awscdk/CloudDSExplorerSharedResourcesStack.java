package software.amazon.awscdk;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.ec2.Vpc;
import software.constructs.Construct;

public class CloudDSExplorerSharedResourcesStack extends Stack {
	private final Vpc vpc;
	public CloudDSExplorerSharedResourcesStack(final Construct parent, final String name) {
		super(parent, name);

		// Create a VPC
		Vpc vpc = Vpc.Builder.create(this, "CloudDSExplorerVPC")
			.maxAzs(2)
			.build();

		this.vpc = vpc;
	}

	public Vpc getVpc() {
		return this.vpc;
	}
}
