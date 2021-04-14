package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class ValidHexValidator implements ConstraintValidator<ValidHex, String> {

    @Override
    public boolean isValid(String color, ConstraintValidatorContext constraintContext) {
        String regex = "^#([A-Fa-f0-9]{6})";
        Pattern p = Pattern.compile(regex);
        return color != null && p.matcher(color).matches();
    }
}
