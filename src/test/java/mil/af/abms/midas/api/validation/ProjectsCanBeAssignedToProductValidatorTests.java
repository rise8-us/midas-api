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
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.helpers.RequestContext;

@ExtendWith(SpringExtension.class)
@Import({ProjectsCanBeAssignedToProductValidator.class})
class ProjectsCanBeAssignedToProductValidatorTests {

    @Autowired
    ProjectsCanBeAssignedToProductValidator validator;
    @MockBean
    private ProjectService projectService;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    private final Product productOne = Builder.build(Product.class)
            .with(p -> p.setId(4L))
            .with(p -> p.setName("New Product")).get();
    private final Project projectWithProduct = Builder.build(Project.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setName("project with product"))
            .with(t -> t.setDescription("has product"))
            .with(t -> t.setProduct(productOne)).get();
    private final Project projectWithOutProduct = Builder.build(Project.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setName("project with null product"))
            .with(t -> t.setDescription("No product")).get();

    @BeforeEach
    void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    void should_validate_project_not_assigned_to_any_product() {
        RequestContext.setRequestContext("id", "4");
        when(projectService.findById(2L)).thenReturn(projectWithOutProduct);

        assertTrue(validator.isValid(Set.of(2L), context));
    }

    @Test
    void should_validate_project_assigned_to_self() {
        RequestContext.setRequestContext("id", "4");
        when(projectService.findById(1L)).thenReturn(projectWithProduct);

        assertTrue(validator.isValid(Set.of(1L), context));
    }

    @Test
    void should_fail_update_product_when_project_assigned_different_product() {
        RequestContext.setRequestContext("id", "5");
        when(projectService.existsById(projectWithProduct.getId())).thenReturn(true);
        when(projectService.findById(projectWithProduct.getId())).thenReturn(projectWithProduct);

        assertFalse(validator.isValid(Set.of(1L), context));
    }

}
