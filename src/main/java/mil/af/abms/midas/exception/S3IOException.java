package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class S3IOException extends AbstractRuntimeException {

    public S3IOException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
