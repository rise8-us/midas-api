package mil.af.abms.midas.api.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import mil.af.abms.midas.exception.InvalidInputParameterException;

public final class TimeConversion {

    private TimeConversion() {
        throw new IllegalStateException("Utility Class");
    }

    public static final String DAY_PATTERN = "yyyy-MM-dd";
    public static final String DAY_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";

    public static LocalDateTime getTime(String timeAsString) {
        timeAsString = Optional.ofNullable(timeAsString).orElseThrow(() -> new InvalidInputParameterException("No date time string provided"));
        return timeFromString(timeAsString);
    }

    public static LocalDateTime getTimeOrNull(String timeAsString) {
        if (timeAsString == null) return null;
        return getTime(timeAsString);
    }

    private static LocalDateTime timeFromString(String value) {
        var formatter = DateTimeFormatter.ofPattern(DAY_TIME_PATTERN);
        value = value.length() == 10 ? value + "T00:00:00" : value;

        try {
            return LocalDateTime.parse(value, formatter);
        } catch (DateTimeParseException e ) {
            throw new InvalidInputParameterException(
                    String.format("Improperly formatted LocalDateTime String, must be %s or %s", DAY_PATTERN, DAY_TIME_PATTERN)
            );
        }
    }


}
