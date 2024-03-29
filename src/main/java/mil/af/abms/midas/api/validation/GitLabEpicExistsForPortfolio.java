package mil.af.abms.midas.api.validation;

import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({ TYPE_USE })
@Retention(RUNTIME)
@Constraint(validatedBy = GitLabEpicExistsWithPortfolioValidator.class)
@Documented
public @interface GitLabEpicExistsForPortfolio {

    String message() default "GitLab epic does not exist or cannot be found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
