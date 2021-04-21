package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;

public class ProjectsCanBeAssignedToProductValidator implements ConstraintValidator<ProjectsCanBeAssignedToProduct, Set<Long>> {

        @Autowired
        private ProjectService projectService;

        @Override
        public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
                constraintContext.disableDefaultConstraintViolation();

                Set<Project> projectWithExistingProducts =
                ids.stream().map(projectService::getObject).filter(p -> p.getProduct() != null).peek(i ->
                        constraintContext.buildConstraintViolationWithTemplate(
                                String.format("Project with id: %s is already assigned to Product with id: %s",
                                        i.getId(), i.getProduct().getId())).addConstraintViolation()
                ).collect(Collectors.toSet());

                return projectWithExistingProducts.isEmpty();
    }
}
