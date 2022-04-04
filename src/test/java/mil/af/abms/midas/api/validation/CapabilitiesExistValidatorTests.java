package mil.af.abms.midas.api.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.capability.CapabilityService;

@ExtendWith(SpringExtension.class)
@Import({CapabilitiesExistValidator.class})
public class CapabilitiesExistValidatorTests {

    @Autowired
    CapabilitiesExistValidator validator;
    @MockBean
    private CapabilityService service;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_product_exists_false() {
        when(service.existsById(3L)).thenReturn(false);

        assertFalse(validator.isValid(Set.of(3L), context));
    }

    @Test
    public void should_validate_product_exists_true() {
        when(service.existsById(1L)).thenReturn(true);

        assertTrue(validator.isValid(Set.of(1L), context));
    }
    
}
