package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class GitApiException extends AbstractRuntimeException {

    public GitApiException(String className, Long id) {
        super(String.format("Failed to find %s with id %d", className, id), HttpStatus.NOT_FOUND);
    }

    public GitApiException(String className, String username) {
        super(String.format("Failed to find %s with username %s", className, username), HttpStatus.NOT_FOUND);
    }


}
