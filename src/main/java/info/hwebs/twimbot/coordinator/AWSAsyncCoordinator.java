package info.hwebs.twimbot.coordinator;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import info.hwebs.twimbot.coordinator.handler.AWSBatchAsyncRequestHandler;
import info.hwebs.twimbot.coordinator.handler.AWSEventBridgeAsyncRequestHandler;
import info.hwebs.twimbot.coordinator.mapper.AWSMapper;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.batch.model.ListJobsResponse;

public class AWSAsyncCoordinator implements AsyncCoordinator {

    private final AWSEventBridgeAsyncRequestHandler eventBridgeHandler;

    private final AWSBatchAsyncRequestHandler batchHandler;

    private final AWSMapper mapper;

    public AWSAsyncCoordinator(AWSEventBridgeAsyncRequestHandler eventBridgeHandler,
            AWSBatchAsyncRequestHandler batchHandler, AWSMapper mapper) {
        this.eventBridgeHandler = eventBridgeHandler;
        this.batchHandler = batchHandler;
        this.mapper = mapper;
    }

    @Override
    public Mono<CronDetail> getCronDetail() {
        return Mono.fromFuture(eventBridgeHandler.handleDescribeRuleRequest().thenApply(mapper::mapCronDetail));

    }

    @Override
    public Mono<List<ExecutionHistory>> getExecutionHistory() {

        /*
         * Becuase AWS only allows us to query one status at a time, we can
         * asynchronously call all the desired statuses and combine the results
         */

        return Mono.fromFuture(
                batchHandler.handleListJobsRequest().map(listJobFuture -> listJobFuture.thenApply(Stream::of))
                        .reduce(CompletableFuture.completedFuture(Stream.empty()),
                                (accumulator, listJobStreamFuture) -> accumulator.thenCombine(listJobStreamFuture,
                                        Stream::concat))
                        .thenApply(jobResponseStream -> jobResponseStream.map(ListJobsResponse::jobSummaryList))
                        .thenApply(jobSummaryListStream -> jobSummaryListStream.flatMap(Collection::stream))
                        .thenApply(jobSummaryStream -> jobSummaryStream.map(mapper::mapExecutionHistory))
                        .thenApply(executionHistoryStream -> executionHistoryStream.collect(Collectors.toList())));
    }

}
