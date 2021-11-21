package info.hwebs.twimbot.coordinator.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import software.amazon.awssdk.services.batch.BatchAsyncClient;
import software.amazon.awssdk.services.batch.model.JobStatus;
import software.amazon.awssdk.services.batch.model.ListJobsRequest;

@ExtendWith(MockitoExtension.class)
public class AWSBatchAsyncRequestHandlerTest {

    @Mock
    BatchAsyncClient client;

    @Captor
    ArgumentCaptor<ListJobsRequest> listJobRequestCaptor;

    private static Stream<Arguments> provideJobStatus() {
        return JobStatus.knownValues().stream().map(Arguments::of);
    }

    private static Stream<Arguments> provideJobStatusSet() {
        return Stream.of(Arguments.of(Collections.emptySet()), Arguments.of(EnumSet.of(JobStatus.SUCCEEDED)),
                Arguments.of(EnumSet.of(JobStatus.FAILED, JobStatus.SUCCEEDED)));
    }

    private AWSBatchAsyncRequestHandler buildRequestHandler() {
        return new AWSBatchAsyncRequestHandler(client, "", Collections.emptySet());
    }

    private AWSBatchAsyncRequestHandler buildRequestHandler(String jobQueue) {
        return new AWSBatchAsyncRequestHandler(client, jobQueue, Collections.emptySet());
    }

    private AWSBatchAsyncRequestHandler buildRequestHandler(Set<JobStatus> statusSet) {
        return new AWSBatchAsyncRequestHandler(client, "", statusSet);
    }

    private <T> void wait(CompletableFuture<T> future) {
        try {
            future.wait();
        } catch (Exception e) {
            // all the futures will be null
        }
    }

    @ParameterizedTest
    @MethodSource("provideJobStatus")
    void testBuildListJobsRequestStatus(JobStatus status) {
        AWSBatchAsyncRequestHandler handler = buildRequestHandler();
        assertEquals(status, handler.buildListJobsRequest(status).jobStatus());
    }

    @ParameterizedTest
    @ValueSource(strings = { "job-queue-1", "job-queue-2" })
    void testBuildListJobsQueueName(String jobQueue) {
        AWSBatchAsyncRequestHandler handler = buildRequestHandler(jobQueue);
        assertEquals(jobQueue, handler.buildListJobsRequest(JobStatus.SUCCEEDED).jobQueue());
    }

    @ParameterizedTest
    @MethodSource("provideJobStatusSet")
    void testHandleListJobsRequestCallsStatusGiven(Set<JobStatus> statusSet) {
        AWSBatchAsyncRequestHandler handler = buildRequestHandler(statusSet);
        handler.handleListJobsRequest().forEach(this::wait);

        verify(client, times(statusSet.size())).listJobs(listJobRequestCaptor.capture());
        assertEquals(statusSet, listJobRequestCaptor.getAllValues().stream().map(ListJobsRequest::jobStatus)
                .collect(Collectors.toSet()));
    }

}
