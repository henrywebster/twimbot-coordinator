package info.hwebs.twimbot.coordinator.handler;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import software.amazon.awssdk.services.batch.BatchAsyncClient;
import software.amazon.awssdk.services.batch.model.JobStatus;
import software.amazon.awssdk.services.batch.model.ListJobsRequest;
import software.amazon.awssdk.services.batch.model.ListJobsResponse;

public class AWSBatchAsyncRequestHandler {

    private final BatchAsyncClient client;

    private final Set<JobStatus> statuses;

    private final String jobQueue;

    public AWSBatchAsyncRequestHandler(BatchAsyncClient client, String jobQueue, Set<JobStatus> statuses) {
        this.client = client;
        this.statuses = statuses;
        this.jobQueue = jobQueue;
    }

    public Stream<CompletableFuture<ListJobsResponse>> handleListJobsRequest() {
        return statuses.stream().map(this::buildListJobsRequest).map(client::listJobs);
    }

    ListJobsRequest buildListJobsRequest(JobStatus status) {
        return ListJobsRequest.builder().jobQueue(jobQueue).jobStatus(status).build();
    }
}
