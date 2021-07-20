package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class IllegalRequestBodyException extends AbstractRuntimeException {
    public IllegalRequestBodyException(String status) {
        super(String.format("Error! Invalid request body: %s", status), HttpStatus.BAD_REQUEST);
    }
}
