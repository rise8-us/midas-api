package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class AssertionException extends AbstractRuntimeException {

    public AssertionException(String assertionType) {
        super(String.format("OGSM must have at least one %s", assertionType), HttpStatus.BAD_REQUEST);
    }

}
