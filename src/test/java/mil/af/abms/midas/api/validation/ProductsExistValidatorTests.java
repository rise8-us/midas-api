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

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;

@ExtendWith(SpringExtension.class)
@Import({ProductsExistValidator.class})
public class ProductsExistValidatorTests {

    @Autowired
    ProductsExistValidator validator;
    @MockBean
    private ProductService productService;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    private final Product product = Builder.build(Product.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("product test"))
            .with(t -> t.setAcronym("New product")).get();

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_product_exists_false() {
        when(productService.existsById(3L)).thenReturn(false);

        assertFalse(validator.isValid(Set.of(3L), context));
    }

    @Test
    public void should_validate_product_exists_true() {
        when(productService.existsById(1L)).thenReturn(true);

        assertTrue(validator.isValid(Set.of(1L), context));
    }
    
}
