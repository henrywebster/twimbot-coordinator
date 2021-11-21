package info.hwebs.twimbot.coordinator;

import java.text.ParseException;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.Period;
import java.time.Year;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CronDetailTest {

    @Test
    void testGetDescription() throws ParseException {
        Assertions.assertEquals("At 17:00, only on Saturday", CronDetail.of("0 0 17 ? * 7").getDescription());
    }

    @Test
    void testGetNext() throws ParseException {
        Assertions.assertEquals(OffsetDateTime.of(Year.now().plus(Period.ofYears(1)).getValue(),
                Month.JANUARY.getValue(), 1, 0, 0, 0, 0, ZoneOffset.UTC).toEpochSecond(),
                CronDetail.of("0 0 0 1 JAN ?").getNext());
    }
}
