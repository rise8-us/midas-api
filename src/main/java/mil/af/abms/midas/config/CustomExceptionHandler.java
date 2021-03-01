package mil.af.abms.midas.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import lombok.Getter;
import mil.af.abms.midas.exception.AbstractRuntimeException;

@ControllerAdvice
public class CustomExceptionHandler {

    private static final Function<Object, String> GENERATE_ERROR_MSG = obj -> "Validation failed. " + obj + " error(s)";

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleAllExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Errors errors = ex.getBindingResult();
        ValidationError error = new ValidationError(GENERATE_ERROR_MSG.apply(errors.getErrorCount()));
        for (ObjectError objectError : errors.getAllErrors()) {
            error.addError(objectError.getDefaultMessage());
        }
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleCustomExceptions(AbstractRuntimeException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", ex.getStatus().value());
        body.put("error", ex.getStatus().getReasonPhrase());
        body.put("path", request.getDescription(false).split("=")[1]);
        return new ResponseEntity<>(body, ex.getStatus());
    }

    @Getter
    public static class ValidationError {

        private final String message;
        private List<String> errors;

        ValidationError(String message) {
            this.message = message;
        }

        void addError(String error) {
            if (this.errors == null) {
                this.errors = new ArrayList<>();
            }
            this.errors.add(error);
        }
    }

}
