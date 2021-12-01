package mil.af.abms.midas.api.comment.dto.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = IsAssertionOrMeasureCommentValidator.class)
@Documented
public @interface IsAssertionOrMeasureComment {

    String message() default "comment must have only an assertion or measure";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
