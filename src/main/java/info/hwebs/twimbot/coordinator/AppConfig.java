package info.hwebs.twimbot.coordinator;

import java.util.EnumSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import info.hwebs.twimbot.coordinator.handler.AWSBatchAsyncRequestHandler;
import info.hwebs.twimbot.coordinator.handler.AWSEventBridgeAsyncRequestHandler;
import info.hwebs.twimbot.coordinator.mapper.AWSMapper;
import software.amazon.awssdk.services.batch.BatchAsyncClient;
import software.amazon.awssdk.services.batch.model.JobStatus;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;

@Configuration
public class AppConfig {

    @Value("${JOB_QUEUE}")
    public String jobQueue;

    @Value("${RULE_NAME}")
    public String ruleName;

    @Bean
    public AsyncCoordinator asyncCoordinator() {

        // TODO should be an env option in future
        final Set<JobStatus> statuses = EnumSet.of(JobStatus.SUCCEEDED);

        final AWSBatchAsyncRequestHandler batchHandler = new AWSBatchAsyncRequestHandler(
                BatchAsyncClient.builder().build(), jobQueue, statuses);

        final AWSEventBridgeAsyncRequestHandler eventBridgeHandler = new AWSEventBridgeAsyncRequestHandler(
                EventBridgeAsyncClient.builder().build(), ruleName);

        return new AWSAsyncCoordinator(eventBridgeHandler, batchHandler, new AWSMapper());
    }

}
