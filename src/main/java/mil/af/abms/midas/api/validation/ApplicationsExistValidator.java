package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.application.ApplicationService;

public class ApplicationsExistValidator implements ConstraintValidator<ApplicationsExist, Set<Long>> {

    @Autowired
    private ApplicationService applicationService;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        Set<Long> nonExistentIds = ids.stream().filter(i -> !applicationService.existsById(i)).peek(i ->
                constraintContext.buildConstraintViolationWithTemplate(
                        String.format("Application with id: %s does not exists", i)
                ).addConstraintViolation()
        ).collect(Collectors.toSet());

        return nonExistentIds.isEmpty();
    }
    
}
