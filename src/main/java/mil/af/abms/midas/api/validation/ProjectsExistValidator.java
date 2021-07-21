package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.project.ProjectService;

public class ProjectsExistValidator implements ConstraintValidator<ProjectsExist, Set<Long>> {

    @Autowired
    private ProjectService projectService;

    @Override
    public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
        constraintContext.disableDefaultConstraintViolation();

        var violations = ids.stream().filter(i -> !projectService.existsById(i)).map(i ->
                constraintContext.buildConstraintViolationWithTemplate(
                        String.format("Project with id: %s does not exist", i)
                ).addConstraintViolation()
        ).collect(Collectors.toSet());

        return violations.isEmpty();
    }

}
