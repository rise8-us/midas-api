package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class IllegalRequestHeadersException extends AbstractRuntimeException {
    public IllegalRequestHeadersException(String status) {
        super(String.format("Error! Invalid use of headers: %s", status), HttpStatus.BAD_REQUEST);
    }
}
