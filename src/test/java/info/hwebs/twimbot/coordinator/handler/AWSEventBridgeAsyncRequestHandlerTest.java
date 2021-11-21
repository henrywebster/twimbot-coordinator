package info.hwebs.twimbot.coordinator.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleRequest;

@ExtendWith(MockitoExtension.class)
public class AWSEventBridgeAsyncRequestHandlerTest {

    @Mock
    EventBridgeAsyncClient client;

    @Captor
    ArgumentCaptor<DescribeRuleRequest> describeRuleRequestCaptor;

    @ParameterizedTest
    @ValueSource(strings = { "rule-1", "rule-2" })
    void testBuildDescribeRuleRequest(String ruleName) {
        AWSEventBridgeAsyncRequestHandler handler = new AWSEventBridgeAsyncRequestHandler(client, ruleName);
        assertEquals(ruleName, handler.buildDescribeRuleRequest().name());
    }

    @ParameterizedTest
    @ValueSource(strings = { "rule-1", "rule-2" })
    void testHandleDescribeRuleRequest(String ruleName) throws InterruptedException {
        AWSEventBridgeAsyncRequestHandler handler = new AWSEventBridgeAsyncRequestHandler(client, ruleName);
        handler.handleDescribeRuleRequest();

        verify(client, times(1)).describeRule(describeRuleRequestCaptor.capture());
        assertEquals(ruleName, describeRuleRequestCaptor.getValue().name());
        assertEquals("default", describeRuleRequestCaptor.getValue().eventBusName());
    }
}
