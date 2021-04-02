package mil.af.abms.midas.api.user.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = TeamsExistValidator.class)
@Documented
public @interface TeamsExist {

    String message() default "team does not exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}