package info.hwebs.twimbot.coordinator;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
public class CoordinatorController {

    @Autowired
    public AsyncCoordinator coordinator;

    // TODO handle errors
    @GetMapping("/cron")
    public Mono<CronDetail> getCron() {
        return coordinator.getCronDetail();
    }

    @GetMapping("/execution")
    public Mono<List<ExecutionHistory>> getExeuctionHistory() {
        return coordinator.getExecutionHistory();
    }
}
