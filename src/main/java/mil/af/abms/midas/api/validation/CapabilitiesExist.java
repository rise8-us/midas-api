package mil.af.abms.midas.api.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = CapabilitiesExistValidator.class)
@Documented
public @interface CapabilitiesExist {

    String message() default "capabilities do not exist by the given Id";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
