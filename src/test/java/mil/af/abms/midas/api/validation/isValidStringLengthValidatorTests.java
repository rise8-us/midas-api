package mil.af.abms.midas.api.validation;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

@ExtendWith(SpringExtension.class)
@Import({IsValidStringLengthValidator.class})
public class isValidStringLengthValidatorTests {

    @Autowired
    IsValidStringLengthValidator validator;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_string_length_true() {
        validator.setMaxLength(20);
        assertTrue(validator.isValid("a short string", context));
    }

    @Test
    public void should_validate_string_length_false() {
        validator.setMaxLength(20);
        assertFalse(validator.isValid("a string that is longer than 20.", context));
    }



}
