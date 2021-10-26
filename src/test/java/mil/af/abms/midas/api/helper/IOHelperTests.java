package mil.af.abms.midas.api.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

class IOHelperTests {

    @Test
    void should_throw_error_if_private_constructor_is_called() throws Exception {
        Class<?> clazz = IOHelper.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        AssertionsForClassTypes.assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    void should_return_total_size_of_stream() throws IOException {
        var byteArray = "This is a string".getBytes(StandardCharsets.UTF_8);
        var inputStream = new ByteArrayInputStream(byteArray);

        assertThat(IOHelper.getInputStreamSize(inputStream)).isEqualTo(byteArray.length);
    }


}
