package mil.af.abms.midas.api.tag.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;

import mil.af.abms.midas.api.tag.TagService;

public class ValidHexValidator implements ConstraintValidator<ValidHex, String> {

    @Autowired
    private TagService tagService;

    @Override
    public boolean isValid(String color, ConstraintValidatorContext constraintContext) {

            String regex = "^#([A-Fa-f0-9]{6})";

            Pattern p = Pattern.compile(regex);

            if (color == null) {
                return false;
            }

            Matcher m = p.matcher(color);

            return m.matches();
        }
    }
