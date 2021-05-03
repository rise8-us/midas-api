package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.ogsm.OgsmService;

public class OgsmExistsValidator implements ConstraintValidator<OgsmExists, Long> {

    @Autowired
    private OgsmService ogsmService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(OgsmExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {
        return id == null ? allowNull : ogsmService.existsById(id);
    }
}
