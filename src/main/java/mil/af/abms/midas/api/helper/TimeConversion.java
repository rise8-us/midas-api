package mil.af.abms.midas.api.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.Optional;

import mil.af.abms.midas.exception.InvalidInputParameterException;

public final class TimeConversion {

    private TimeConversion() {
        throw new IllegalStateException("Utility Class");
    }

    public static final String YEAR_MONTH_PATTERN = "yyyy-MM";
    public static final String YEAR_MONTH_DAY_PATTERN = "yyyy-MM-dd";
    public static final String YEAR_MONTH_DAY_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static final DateTimeFormatter  YEAR_MONTH_DAY_TIME_FORMAT = DateTimeFormatter.ofPattern(YEAR_MONTH_DAY_TIME_PATTERN);
    public static final DateTimeFormatter  YEAR_MONTH_DAY_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern(YEAR_MONTH_DAY_PATTERN)
            .parseDefaulting(ChronoField.HOUR_OF_DAY,0)
            .toFormatter();
    public static final DateTimeFormatter YEAR_MONTH_FORMAT = new DateTimeFormatterBuilder()
            .appendPattern(YEAR_MONTH_PATTERN)
            .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
            .parseDefaulting(ChronoField.HOUR_OF_DAY,0)
            .toFormatter();

    private static final Map<Integer,DateTimeFormatter> formatters = Map.ofEntries(
            Map.entry(7, YEAR_MONTH_FORMAT),
            Map.entry(10, YEAR_MONTH_DAY_FORMAT),
            Map.entry(19, YEAR_MONTH_DAY_TIME_FORMAT)
    );

    public static LocalDateTime getTime(String timeAsString) {
        timeAsString = Optional.ofNullable(timeAsString).orElseThrow(() -> new InvalidInputParameterException("No date time string provided"));
        return timeFromString(timeAsString);
    }

    public static LocalDateTime getTimeOrNull(String timeAsString) {
        if (timeAsString == null) return null;
        return getTime(timeAsString);
    }

    private static LocalDateTime timeFromString(String value) {

        try {
            return LocalDateTime.parse(value, formatters.getOrDefault(value.length(),YEAR_MONTH_DAY_TIME_FORMAT));
        } catch (DateTimeParseException e) {
            throw new InvalidInputParameterException(
                    String.format("Improperly formatted LocalDateTime String, must be %s, %s, %s", YEAR_MONTH_PATTERN, YEAR_MONTH_DAY_PATTERN, YEAR_MONTH_DAY_TIME_PATTERN)
            );
        }
    }


}
