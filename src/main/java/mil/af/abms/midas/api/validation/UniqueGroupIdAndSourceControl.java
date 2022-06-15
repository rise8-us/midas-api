package mil.af.abms.midas.api.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = UniqueGroupIdAndSourceControlValidator.class)
public @interface UniqueGroupIdAndSourceControl {

    String message() default "Gitlab Group ID and Server combination already used";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
