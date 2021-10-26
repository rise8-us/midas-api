package mil.af.abms.midas.api.helper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.Test;

class GzipHelperTests {

    @Test
    void should_throw_error_if_private_constructor_is_called() throws Exception {
        Class<?> clazz = GzipHelper.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        AssertionsForClassTypes.assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    void should_compress_string_to_input_stream() throws IOException {
        var resourceName = "src/test/resources/condition.json";
        var conditionStr = Files.readString(Path.of(resourceName));
        var uncompressedStream = new ByteArrayInputStream(conditionStr.getBytes(StandardCharsets.UTF_8));

        assertThat(GzipHelper.compressStringToInputStream(conditionStr).readAllBytes())
                .hasSizeLessThan(uncompressedStream.readAllBytes().length);
    }

    @Test
    void should_decompress_input_stream_to_string() throws IOException {
        var resourceName = "src/test/resources/condition.json";
        var conditionStr = Files.readString(Path.of(resourceName));
        var compressedStream = GzipHelper.compressStringToInputStream(conditionStr);

        assertThat(GzipHelper.decompressInputStreamToString(compressedStream)).isEqualTo(conditionStr);
    }

}
