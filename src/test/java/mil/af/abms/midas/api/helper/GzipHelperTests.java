package mil.af.abms.midas.api.helper;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

public class GzipHelperTests {

    @Test
    void should_compress_string_to_input_stream() throws IOException {
        var resourceName = "src/test/resources/condition.json";
        var conditionStr = Files.readString(Path.of(resourceName));
        var uncompressedStream = new ByteArrayInputStream(conditionStr.getBytes(StandardCharsets.UTF_8));

        assertThat(GzipHelper.compressStringToInputStream(conditionStr).readAllBytes().length)
                .isLessThan(uncompressedStream.readAllBytes().length);
    }

    @Test
    void should_decompress_input_stream_to_string() throws IOException {
        var resourceName = "src/test/resources/condition.json";
        var conditionStr = Files.readString(Path.of(resourceName));
        var compressedStream = GzipHelper.compressStringToInputStream(conditionStr);

        assertThat(GzipHelper.decompressInputStreamToString(compressedStream)).isEqualTo(conditionStr);
    }

}
