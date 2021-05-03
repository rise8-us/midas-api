package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.assertion.AssertionService;

public class AssertionExistsValidator implements ConstraintValidator<AssertionExists, Long> {

    @Autowired
    private AssertionService assertionService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(AssertionExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {
        return id == null ? allowNull : assertionService.existsById(id);
    }
}
