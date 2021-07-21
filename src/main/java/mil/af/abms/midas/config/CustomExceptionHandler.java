package mil.af.abms.midas.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.exception.AbstractRuntimeException;

@ControllerAdvice
@Slf4j
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

    @ResponseBody
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ValidationError> handleExceptions(HttpMessageNotReadableException ex, WebRequest request) {
        ValidationError error = new ValidationError(ex.getLocalizedMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AbstractRuntimeException.class)
    public ResponseEntity<Object> handleCustomExceptions(AbstractRuntimeException ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("status", ex.getStatus().value());
        body.put("error", ex.getStatus().getReasonPhrase());
        body.put("path", request.getDescription(false).split("=")[1]);
        return new ResponseEntity<>(body, ex.getStatus());
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(Exception ex) {
        log.error(ex.getLocalizedMessage(), ex);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Getter
    public static class ValidationError {

        private final String message;
        private List<String> errors;

        ValidationError(String message) {
            this.message = message;
        }

        void addError(String error) {
            this.errors = Optional.ofNullable(this.errors).orElse(new ArrayList<>());
            this.errors.add(error);
        }
    }

}
