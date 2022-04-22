package mil.af.abms.midas.api.validation;


import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.gantt.target.TargetService;

public class TargetExistsValidator implements ConstraintValidator<TargetExists, Long> {

    @Autowired
    private TargetService targetService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(TargetExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {
        return Optional.ofNullable(id).map(targetService::existsById).orElse(allowNull);
    }
}
