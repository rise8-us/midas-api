package mil.af.abms.midas.api.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.exception.S3ClientException;

@Slf4j
public class GzipHelper {

    private GzipHelper() {
        throw new IllegalStateException("Utility Class");
    }

    public static InputStream compressStringToInputStream(String data) {
        var dataToCompress = data.getBytes(StandardCharsets.UTF_8);
        var outByteStream = new ByteArrayOutputStream(dataToCompress.length);

        try (
                outByteStream;
                var zipStream = new GZIPOutputStream(outByteStream);
        ) {
            zipStream.write(dataToCompress);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return new ByteArrayInputStream(outByteStream.toByteArray());
    }

    public static String decompressInputStreamToString(InputStream gzipInputStream) {
        try {
            var inputStream = new GZIPInputStream(gzipInputStream);

            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new S3ClientException("Failed to read file");
        }
    }
}
