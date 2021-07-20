package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class UnexpectedEncodingException extends AbstractRuntimeException {
    public UnexpectedEncodingException(String status) {
        super(String.format("Error! Unexpected encoding on user input: %s", status), HttpStatus.BAD_REQUEST);
    }
}
