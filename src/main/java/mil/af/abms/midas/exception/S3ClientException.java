package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class S3ClientException extends AbstractRuntimeException {

    public S3ClientException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }

}
