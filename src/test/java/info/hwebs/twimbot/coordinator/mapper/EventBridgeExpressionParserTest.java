package info.hwebs.twimbot.coordinator.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

public class EventBridgeExpressionParserTest {

    private static Stream<Arguments> provideExpressions() {
        return Stream.of(Arguments.of("0 0 17 ? * 7", "cron(0 17 ? * 7 *)"),
                Arguments.of("0 0 10 ? * 7", "cron(0 10 ? * 7 1995)"),
                Arguments.of("0 0 0 10 ? * 7", "cron(0 0 10 ? * 7 *)")); // assumption: expression is from
                                                                         // EventBridge scheduleExpression
    }

    @ParameterizedTest
    @MethodSource("provideExpressions")
    void testEventBridgeExpressionParser(String expected, String expression) {
        assertEquals(expected, EventBridgeExpressionParser.parse(expression));
    }

    @ParameterizedTest
    @ValueSource(strings = { "", "0 0 10 ? * 7" })
    void testEventBrudgeExpressionParserInvalid(String expression) {
        assertThrows(IllegalStateException.class, () -> EventBridgeExpressionParser.parse(expression));
    }

}
