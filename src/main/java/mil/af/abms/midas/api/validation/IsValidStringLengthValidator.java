package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import lombok.Setter;

public class IsValidStringLengthValidator implements ConstraintValidator<IsValidStringLength, String> {

    @Setter
    private int maxLength;

    @Override
    public void initialize(IsValidStringLength constraintAnnotation) { this.maxLength = constraintAnnotation.maxLength(); }

    @Override
    public boolean isValid(String string, ConstraintValidatorContext constraintContext) {
        constraintContext.buildConstraintViolationWithTemplate("Max characters: " + maxLength).addConstraintViolation();

        return string == null || string.length() <= maxLength;
    }
}
