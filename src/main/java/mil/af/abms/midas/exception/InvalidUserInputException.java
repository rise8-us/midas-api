package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class InvalidUserInputException extends AbstractRuntimeException {
    public InvalidUserInputException(String status) {
        super(String.format("Error! Invalid Request: %s", status), HttpStatus.BAD_REQUEST);
    }
}
