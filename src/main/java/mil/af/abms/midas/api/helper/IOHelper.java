package mil.af.abms.midas.api.helper;

import java.io.IOException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IOHelper {

    private IOHelper() {
        throw new IllegalStateException("Utility Class");
    }

    public static long getInputStreamSize(InputStream inputStream) throws IOException {
        var length = inputStream.readAllBytes().length;
        inputStream.reset();
        return length;
    }

}
