package mil.af.abms.midas.api.helper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.exception.InvalidInputParameterException;

class TimeConversionTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.of(2021, 10, 1, 0, 0);

    @Test
    void should_throw_error_if_private_constructor_is_called() throws Exception {
        Class<?> clazz = TimeConversion.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

   @Test
   void should_getTime_from_string() {
        assertThat(TEST_TIME).isEqualTo(TimeConversion.getTime("2021-10-01T00:00:00"));
        assertThat(TEST_TIME).isEqualTo(TimeConversion.getTime("2021-10-01"));
        assertThrows(InvalidInputParameterException.class, () -> TimeConversion.getTime(null));
        assertThrows(InvalidInputParameterException.class, () -> TimeConversion.getTime("2021-"));
   }

   @Test
   void should_getTimeOrNull_from_string() {
        assertThat(TEST_TIME).isEqualTo(TimeConversion.getTimeOrNull("2021-10-01T00:00:00"));
        assertThat(TEST_TIME).isEqualTo(TimeConversion.getTimeOrNull("2021-10-01"));
        assertNull(TimeConversion.getTimeOrNull(null));
        assertThrows(InvalidInputParameterException.class, () -> TimeConversion.getTimeOrNull("2021-"));
   }

   @Test
   void should_convert() {

        assertThat(TEST_TIME).isEqualTo(TimeConversion.getTime("2021-10"));

   }

}
