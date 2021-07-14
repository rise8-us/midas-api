package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.helper.HttpPathVariableIdGrabber;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;

public class ProjectsCanBeAssignedToProductValidator implements ConstraintValidator<ProjectsCanBeAssignedToProduct, Set<Long>> {

        @Autowired
        private ProjectService projectService;

        @Override
        public boolean isValid(Set<Long> ids, ConstraintValidatorContext constraintContext) {
                constraintContext.disableDefaultConstraintViolation();



                Set<Project> projectWithExistingProducts =
                ids.stream()
                        .map(projectService::findById)
                        .filter(p -> p.getProduct() != null)
                        .filter(p -> !p.getProduct().getId().equals(HttpPathVariableIdGrabber.getPathId()))
                        .peek(i -> constraintContext.buildConstraintViolationWithTemplate(
                                String.format("Project %s already assigned to Product %s",
                                        i.getName(), i.getProduct().getName())).addConstraintViolation()
                ).collect(Collectors.toSet());

                return projectWithExistingProducts.isEmpty();
    }
}
