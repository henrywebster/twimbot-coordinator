package info.hwebs.twimbot.coordinator;

import java.text.ParseException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;

public class CronDetail {

    private final String description;

    private final Cron cron;

    private static final CronDefinition DEFINITION = CronDefinitionBuilder.defineCron().withSeconds().and()
            .withMinutes().and().withHours().and().withDayOfMonth().supportsLW().supportsQuestionMark().and()
            .withMonth().and().withDayOfWeek().withMondayDoWValue(2).withValidRange(1, 7).supportsHash().supportsL()
            .supportsQuestionMark().and().instance();

    public CronDetail(String description, Cron cron) {
        this.description = description;
        this.cron = cron;
    }

    public String getDescription() {
        return description;
    }

    // TODO: what in case there are no future executions -- this should not happen
    // because year can't be specified
    public Long getNext() {
        return ExecutionTime.forCron(cron).nextExecution(ZonedDateTime.now(ZoneOffset.UTC)).get().toEpochSecond();
    }

    public static CronDetail of(String expression) {
        Options options = new Options();
        options.setZeroBasedDayOfWeek(false);
        options.setTwentyFourHourTime(true);
        try {
            return new CronDetail(CronExpressionDescriptor.getDescription(expression, options),
                    new CronParser(DEFINITION).parse(expression));
        } catch (ParseException e) {
            throw new IllegalArgumentException(expression, e);
        }
    }

}
