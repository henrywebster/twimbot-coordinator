package info.hwebs.twimbot.coordinator.mapper;

import info.hwebs.twimbot.coordinator.CronDetail;
import info.hwebs.twimbot.coordinator.ExecutionHistory;
import software.amazon.awssdk.services.batch.model.JobSummary;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleResponse;

public class AWSMapper {

    public CronDetail mapCronDetail(DescribeRuleResponse response) {
        return CronDetail.of(EventBridgeExpressionParser.parse(response.scheduleExpression()));
    }

    public ExecutionHistory mapExecutionHistory(JobSummary summary) {
        return new ExecutionHistory(summary.statusAsString(), summary.statusReason());
    }

}
