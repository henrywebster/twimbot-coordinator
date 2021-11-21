package info.hwebs.twimbot.coordinator;

import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import info.hwebs.twimbot.coordinator.handler.AWSBatchAsyncRequestHandler;
import info.hwebs.twimbot.coordinator.handler.AWSEventBridgeAsyncRequestHandler;
import info.hwebs.twimbot.coordinator.mapper.AWSMapper;
import software.amazon.awssdk.services.batch.model.JobStatus;
import software.amazon.awssdk.services.batch.model.JobSummary;
import software.amazon.awssdk.services.batch.model.ListJobsResponse;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleResponse;

@ExtendWith(MockitoExtension.class)
public class AWSAsyncCoordinatorTest {

    @Mock
    AWSEventBridgeAsyncRequestHandler mockEventBridgeHandler;

    @Mock
    AWSBatchAsyncRequestHandler mockBatchHandler;

    private static JobSummary buildJobSummary(JobStatus status) {
        return JobSummary.builder().status(status).build();
    }

    private static Stream<Arguments> provideJobSummaryList() {
        return Stream.of(Collections.emptyList(), Collections.singletonList(buildJobSummary(JobStatus.SUCCEEDED)))
                .map(Arguments::of);
    }

    private AsyncCoordinator buildAsyncCoordinator() {
        return new AWSAsyncCoordinator(mockEventBridgeHandler, mockBatchHandler, new AWSMapper());
    }

    @Test
    void testGetCronDetail() {
        DescribeRuleResponse response = DescribeRuleResponse.builder().scheduleExpression("cron(0 17 ? * 7 *)").build();

        when(mockEventBridgeHandler.handleDescribeRuleRequest())
                .thenReturn(CompletableFuture.completedFuture(response));

        final AsyncCoordinator coordinator = buildAsyncCoordinator();
        Assertions.assertEquals("At 17:00, only on Saturday", coordinator.getCronDetail().block().getDescription());

    }

    @ParameterizedTest
    @MethodSource("provideJobSummaryList")
    void testGetExecutionHistory(List<JobSummary> jobSummaryList) {

        ListJobsResponse response = ListJobsResponse.builder().jobSummaryList(jobSummaryList).build();
        when(mockBatchHandler.handleListJobsRequest())
                .thenReturn(Stream.of(CompletableFuture.completedFuture(response)));

        final AsyncCoordinator coordinator = buildAsyncCoordinator();
        Assertions.assertEquals(jobSummaryList.size(), coordinator.getExecutionHistory().block().size());
    }
}
