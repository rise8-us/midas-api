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

import mil.af.abms.midas.api.ogsm.OgsmService;

@ExtendWith(SpringExtension.class)
@Import({OgsmExistsValidator.class})
public class OgsmExistsValidatorTests {

    @Autowired
    OgsmExistsValidator validator;
    @MockBean
    private OgsmService ogsmService;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_ogsm_exists_false() {
        when(ogsmService.existsById(3L)).thenReturn(false);

        assertFalse(validator.isValid(3L, context));
    }

    @Test
    public void should_validate_ogsm_exists_true() {
        when(ogsmService.existsById(1L)).thenReturn(true);

        assertTrue(validator.isValid(1L, context));
    }

    @Test
    public void should_validate_true_when_ogsm_id_is_null() {
        validator.setAllowNull(true);

        assertTrue(validator.isValid(null, context));
    }

    @Test
    public void should_validate_false_when_ogsm_id_is_null() {
        validator.setAllowNull(false);
        assertFalse(validator.isValid(null, context));
    }
}
