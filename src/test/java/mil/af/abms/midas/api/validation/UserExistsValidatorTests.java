package mil.af.abms.midas.api.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.user.UserService;

@ExtendWith(SpringExtension.class)
@Import({UserExistsValidator.class})
public class UserExistsValidatorTests {

    @Autowired
    UserExistsValidator validator;
    @MockBean
    private UserService userService;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_user_exists_false() {
        when(userService.existsById(55L)).thenReturn(false);

        assertFalse(validator.isValid(3L, context));
    }

    @Test
    public void should_validate_user_exists_true() {
        when(userService.existsById(1L)).thenReturn(true);

        assertTrue(validator.isValid(1L, context));
    }

    @Test
    public void should_validate_true_when_user_id_is_null() {
        validator.setAllowNull(true);

        assertTrue(validator.isValid(null, context));
    }

    @Test
    public void should_validate_false_when_user_id_is_null() {
        validator.setAllowNull(false);
        assertFalse(validator.isValid(null, context));
    }

}
