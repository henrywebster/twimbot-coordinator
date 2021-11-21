package info.hwebs.twimbot.coordinator.handler;

import java.util.concurrent.CompletableFuture;

import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleRequest;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleResponse;

public class AWSEventBridgeAsyncRequestHandler {

    private final EventBridgeAsyncClient client;

    private final String ruleName;

    public AWSEventBridgeAsyncRequestHandler(EventBridgeAsyncClient client, String ruleName) {
        this.client = client;
        this.ruleName = ruleName;
    }

    public CompletableFuture<DescribeRuleResponse> handleDescribeRuleRequest() {
        return client.describeRule(buildDescribeRuleRequest());
    }

    DescribeRuleRequest buildDescribeRuleRequest() {
        return DescribeRuleRequest.builder().name(ruleName).eventBusName("default").build();
    }

}
