package info.hwebs.twimbot.coordinator;

import java.util.List;

import reactor.core.publisher.Mono;

public interface AsyncCoordinator {

    public Mono<CronDetail> getCronDetail();

    public Mono<List<ExecutionHistory>> getExecutionHistory();
}
