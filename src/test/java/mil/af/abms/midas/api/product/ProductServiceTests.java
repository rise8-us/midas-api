package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductIsArchivedDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(ProductService.class)
public class ProductServiceTests {

    @Autowired
    ProductService productService;
    @MockBean
    UserService userService;
    @MockBean
    ProjectService projectService;
    @MockBean
    TagService tagService;
    @MockBean
    ProductRepository productRepository;
    @Captor
    ArgumentCaptor<Product> productCaptor;

    User user = Builder.build(User.class)
            .with(u -> u.setId(3L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("Lambo")).get();
    Project project = Builder.build(Project.class)
            .with(p -> p.setId(4L))
            .with(p -> p.setName("backend")).get();
    Product product = Builder.build(Product.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setName("Midas")).get();
    Product parent = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Metrics")).get();

    @Test
    public void should_create_product() {
        CreateProductDTO createProductDTO = new CreateProductDTO("homeOne", "new name",
                3L, 1L, Set.of(4L), Set.of(3L));

        when(userService.findByIdOrNull(3L)).thenReturn(user);
        when(projectService.getObject(anyLong())).thenReturn(project);
        when(productRepository.findById(1L)).thenReturn(Optional.of(parent));
        when(productRepository.save(any())).thenReturn(product);
        doNothing().when(projectService).addProductToProjects(any(), any());

        productService.create(createProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(createProductDTO.getName());
        assertThat(productSaved.getProductManager().getId()).isEqualTo(createProductDTO.getProductManagerId());
        assertThat(productSaved.getDescription()).isEqualTo(createProductDTO.getDescription());
        assertFalse(productSaved.getIsArchived());

    }

    @Test
    public void should_find_by_name() {
        when(productRepository.findByName("Midas")).thenReturn(Optional.of(product));

        assertThat(productService.findByName("Midas")).isEqualTo(product);
    }

    @Test
    public void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                productService.findByName("buffet"));
    }

    @Test
    public void should_update_product_by_id() {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO("oneHome", "taxable",
                user.getId(), 1L, Set.of(project.getId()), Set.of(3L));

        when(userService.findByIdOrNull(user.getId())).thenReturn(user);
        when(projectService.getObject(anyLong())).thenReturn(project);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(5L, updateProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(updateProductDTO.getName());
        assertThat(productSaved.getProductManager().getId()).isEqualTo(updateProductDTO.getProductManagerId());
        assertThat(productSaved.getDescription()).isEqualTo(updateProductDTO.getDescription());
        assertThat(productSaved.getProjects()).isEqualTo(Set.of(project));
    }

    @Test
    public void should_update_is_archived_by_id() {
        Product productWithProject = new Product();
        BeanUtils.copyProperties(product, productWithProject);
        productWithProject.setProjects(Set.of(project));

        UpdateProductIsArchivedDTO updateDTO = Builder.build(UpdateProductIsArchivedDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(productWithProject));
        when(productRepository.save(any())).thenReturn(productWithProject);
        when(projectService.archive(any(), any())).thenReturn(project);

        productService.updateIsArchivedById(5L, updateDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertTrue(productSaved.getIsArchived());
    }

    @Test
    public void should_create_product_with_null_product_manager_and_null_portfolio_id() {
        CreateProductDTO createDTO = new CreateProductDTO("name", "description",
                null, null, Set.of(1L), Set.of(1L));

        when(productRepository.save(any())).thenReturn(product);
        doNothing().when(projectService).addProductToProjects(any(), any());

        productService.create(createDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();
        assertThat(productSaved.getProductManager()).isEqualTo(null);
        assertThat(productSaved.getParent()).isEqualTo(null);
    }

    @Test
    public void should_update_product_with_null_product_manager_and_null_portfolio_id() {
        UpdateProductDTO updateDTO = new UpdateProductDTO("name", "description",
                null, null, Set.of(1L), Set.of(1L));

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(5L, updateDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getProductManager()).isEqualTo(null);
        assertThat(productSaved.getParent()).isEqualTo(null);
    }

}
