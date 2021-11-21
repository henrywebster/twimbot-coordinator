package info.hwebs.twimbot.coordinator.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import info.hwebs.twimbot.coordinator.ExecutionHistory;
import software.amazon.awssdk.services.batch.model.JobStatus;
import software.amazon.awssdk.services.batch.model.JobSummary;
import software.amazon.awssdk.services.eventbridge.model.DescribeRuleResponse;

public class AWSMapperTest {

    private static Stream<Arguments> provideExpressions() {
        return Stream.of(Arguments.of("At 17:00, only on Saturday", "cron(0 17 ? * 7 *)"),
                Arguments.of("At 12:00, only on Friday", "cron(0 12 ? * 6 *)"));
    }

    private static Stream<Arguments> provideJobSummaries() {
        return Stream.of(
                Arguments.of("SUCCEEDED", "Job completed", buildJobSummary(JobStatus.SUCCEEDED, "Job completed")),
                Arguments.of("FAILED", "No resources available",
                        buildJobSummary(JobStatus.FAILED, "No resources available")));
    }

    private static JobSummary buildJobSummary(JobStatus status, String description) {
        return JobSummary.builder().status(status).statusReason(description).build();
    }

    @ParameterizedTest
    @MethodSource("provideExpressions")
    void testMapCronDetail(String expected, String expression) {
        AWSMapper mapper = new AWSMapper();
        assertEquals(expected, mapper
                .mapCronDetail(DescribeRuleResponse.builder().scheduleExpression(expression).build()).getDescription());
    }

    @ParameterizedTest
    @MethodSource("provideJobSummaries")
    void testMapExecutionHistory(String expectedStatus, String expectedDescription, JobSummary summary) {
        AWSMapper mapper = new AWSMapper();

        ExecutionHistory result = mapper.mapExecutionHistory(summary);
        assertEquals(expectedStatus, result.getStatus());
        assertEquals(expectedDescription, result.getDescription());
    }

}
