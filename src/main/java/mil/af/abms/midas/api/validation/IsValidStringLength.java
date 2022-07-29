package mil.af.abms.midas.api.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = IsValidStringLengthValidator.class)
@Documented
public @interface IsValidStringLength {

    String message() default "Too many characters. Max length: ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int maxLength();
}
