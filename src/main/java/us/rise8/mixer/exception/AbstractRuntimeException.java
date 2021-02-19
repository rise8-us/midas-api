package us.rise8.mixer.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AbstractRuntimeException extends RuntimeException {

    private final HttpStatus status;

    public AbstractRuntimeException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }

    public AbstractRuntimeException(String msg) {
        super(msg);
        this.status = null;
    }

}
