package mil.af.abms.midas.api.product.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

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

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.ProductEntity;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.exception.EntityNotFoundException;
import mil.af.abms.midas.helpers.RequestContext;

@ExtendWith(SpringExtension.class)
@Import({UniqueNameValidator.class})
public class UniqueNameValidatorTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final ProductEntity foundProduct = Builder.build(ProductEntity.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setDescription("MIDAS Project"))
            .with(p -> p.setGitlabProjectId(2L))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .with(p -> p.setIsArchived(false)).get();

    @Autowired
    UniqueNameValidator validator;
    @MockBean
    private ProductService productService;
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
    public void should_Validate_New_Product_True() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(true);

        when(productService.findByName("MIDAS")).thenThrow(new EntityNotFoundException("Product"));

        assertTrue(validator.isValid(foundProduct.getName(), context));
    }

    @Test
    public void should_Validate_New_Product_False() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(true);

        when(productService.findByName("MIDAS")).thenReturn(foundProduct);

        assertFalse(validator.isValid(foundProduct.getName(), context));
    }

    @Test
    public void should_Validate_Update_Product_True() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(false);

        when(productService.findByName(any())).thenReturn(foundProduct);

        assertTrue(validator.isValid(foundProduct.getName(), context));
    }

    @Test
    public void should_Validate_Update_Product_False() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(false);

        when(productService.findByName(any())).thenReturn(foundProduct);

        assertFalse(validator.isValid(foundProduct.getName(), context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}