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
@Constraint(validatedBy = ProjectsExistValidator.class)
@Documented
public @interface ProjectsExist {

    String message() default "project does not exists";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
