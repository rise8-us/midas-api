package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class InvalidInputParameterException extends AbstractRuntimeException {
    public InvalidInputParameterException(String status) {
        super(String.format("Error! Invalid input parameter: %s", status), HttpStatus.BAD_REQUEST);
    }
}
