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
import org.springframework.web.context.request.RequestContextHolder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.tag.TagService;

@ExtendWith(SpringExtension.class)
@Import({ValidHexValidator.class})
public class ValidHexValidatorTest {

    @Autowired
    ValidHexValidator validator;
    @MockBean
    TagService tagService;
    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @AfterEach
    public void tearDown() {
        clearRequestContext();
    }

    @Test
    public void should_validate_hex_true() {
        assertTrue(validator.isValid("#000000", context));
    }

    @Test
    public void should_validate_hex_false() {
        assertFalse(validator.isValid("#000", context));
    }

    @Test
    public void should_validate_hex_false_null() {
        assertFalse(validator.isValid(null, context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }

}
