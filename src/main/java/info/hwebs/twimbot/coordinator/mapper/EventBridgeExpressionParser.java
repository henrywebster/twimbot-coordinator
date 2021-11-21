package info.hwebs.twimbot.coordinator.mapper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventBridgeExpressionParser {

    private final static Pattern PATTERN = Pattern.compile("^(cron\\()(.*)([ ]+.*\\))$");

    public static String parse(String expression) {
        Matcher m = PATTERN.matcher(expression);
        m.find();
        return "0 " + m.group(2);
    }

}
