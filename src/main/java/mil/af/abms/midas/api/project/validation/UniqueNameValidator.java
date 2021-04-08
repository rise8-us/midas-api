package mil.af.abms.midas.api.project.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.helper.HttpPathVariableIdGrabber;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class UniqueNameValidator implements ConstraintValidator<UniqueName, String> {

    @Autowired
    private ProjectService projectService;

    @Setter
    private boolean isNew;

    @Override
    public void initialize(UniqueName constraintAnnotation) {
        this.isNew = constraintAnnotation.isNew();
    }

    @Override
    public boolean isValid(String name, ConstraintValidatorContext constraintContext) {
        try {
            Project existingProject = projectService.findByName(name);
            if (isNew) {
                return false;
            } else {
                return HttpPathVariableIdGrabber.getPathId().equals(existingProject.getId());
            }
        } catch (EntityNotFoundException e) {
            return true;
        }
    }
}
