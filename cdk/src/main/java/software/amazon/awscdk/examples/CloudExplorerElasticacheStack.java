package software.amazon.awscdk.examples;

import software.amazon.awscdk.CfnOutput;
import software.amazon.awscdk.CfnOutputProps;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.Fn;
import software.amazon.awscdk.services.ec2.*;
import software.amazon.awscdk.services.elasticache.*;
import software.constructs.Construct;

import java.util.Collections;
import java.util.stream.Collectors;

public class CloudExplorerElasticacheStack extends Stack {
	public final CfnOutput redisEndpoint;
	public CloudExplorerElasticacheStack(final Construct parent, final String name, Vpc vpc) {
		super(parent, name);

		// Import the VPC ID from CloudExplorerSharedResourcesStack


		// Create a Redis Subnet Group
		CfnSubnetGroup subnetGroup = CfnSubnetGroup.Builder.create(this, "RedisSubnetGroup")
						.cacheSubnetGroupName("RedisSubnetGroup")
						.description("Subnet group for Redis")
						.subnetIds(vpc.getPrivateSubnets().stream().map(
										subnet -> subnet.getSubnetId()).collect(Collectors.toList()))
						.build();

		// Create an ElastiCache Redis Cluster
		CfnCacheCluster redisCluster = CfnCacheCluster.Builder.create(this, "CloudExplorerRedisCluster")
						.cacheNodeType("cache.t2.micro")
						.engine("redis")
						.numCacheNodes(1)
						//TODO: Refactor to not use default security group. Should create a security group specific to Redis
						.vpcSecurityGroupIds(Collections.singletonList(vpc.getVpcDefaultSecurityGroup()))
						.cacheSubnetGroupName(subnetGroup.getCacheSubnetGroupName())
						.build();

		redisCluster.addDependency(subnetGroup);

		// Export the Redis Endpoint
		this.redisEndpoint = new CfnOutput(this, "RedisEndpoint", CfnOutputProps.builder()
			.value(redisCluster.getAttrRedisEndpointAddress())
			.exportName("RedisEndpointExport")
			.build());

	}
}
