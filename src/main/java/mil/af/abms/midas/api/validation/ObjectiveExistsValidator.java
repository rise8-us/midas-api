package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.objective.ObjectiveService;

public class ObjectiveExistsValidator implements ConstraintValidator<ObjectiveExists, Long> {

    @Autowired
    private ObjectiveService objectiveService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(ObjectiveExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {
        return id == null ? allowNull : objectiveService.existsById(id);
    }
}
