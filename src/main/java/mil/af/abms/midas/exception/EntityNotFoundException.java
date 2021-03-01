package mil.af.abms.midas.exception;

import org.springframework.http.HttpStatus;

public class EntityNotFoundException extends AbstractRuntimeException {

    public EntityNotFoundException(String className) {
        super(String.format("Failed to find %s", className.replace("Entity", "")), HttpStatus.NOT_FOUND);
    }

    public EntityNotFoundException(String className, Long id) {
        super(String.format("Failed to find %s with id %d", className.replace("Entity", ""), id), HttpStatus.NOT_FOUND);
    }

    public EntityNotFoundException(String className, String columnName, String search) {
        super(String.format("Failed to find %s by %s: %s", className.replace("Entity", ""), columnName, search), HttpStatus.NOT_FOUND);
    }

}
