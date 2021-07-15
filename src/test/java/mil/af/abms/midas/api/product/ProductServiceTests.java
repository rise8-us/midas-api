package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.gitlabconfig.GitlabConfig;
import mil.af.abms.midas.api.gitlabconfig.GitlabConfigService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductIsArchivedDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.ProductType;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(ProductService.class)
class ProductServiceTests {

    @SpyBean
    ProductService productService;
    @MockBean
    GitlabConfigService gitlabConfigService;
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

    private final User user = Builder.build(User.class)
            .with(u -> u.setId(3L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("Lambo")).get();
    private final Project project = Builder.build(Project.class)
            .with(p -> p.setId(4L))
            .with(p -> p.setName("backend")).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setName("Midas")).get();
    private final Product parent = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setChildren(Set.of(product)))
            .with(p -> p.setName("Metrics")).get();
    private final Product child = Builder.build(Product.class)
            .with(p -> p.setId(6L))
            .with(p -> p.setName("Metrics")).get();
    private final GitlabConfig gitlabConfig = Builder.build(GitlabConfig.class)
            .with(g -> g.setId(42L))
            .with(g -> g.setName("Mock IL2"))
            .get();

    @Test
    void should_create_product() {
        CreateProductDTO createProductDTO = new CreateProductDTO("homeOne", "new name",
                3L, 1L, Set.of(4L), Set.of(3L), Set.of(child.getId()), ProductType.PRODUCT, 454, 42L);

        when(gitlabConfigService.findByIdOrNull(createProductDTO.getGitlabConfigId())).thenReturn(gitlabConfig);
        when(userService.findByIdOrNull(3L)).thenReturn(user);
        doReturn(child).when(productService).findById(child.getId());
        when(projectService.findById(anyLong())).thenReturn(project);
        doReturn(parent).when(productService).findById(parent.getId());
        when(productRepository.save(any())).thenReturn(product);
        doNothing().when(projectService).addProductToProjects(any(), any());

        productService.create(createProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(createProductDTO.getName());
        assertThat(productSaved.getProductManager().getId()).isEqualTo(createProductDTO.getProductManagerId());
        assertThat(productSaved.getDescription()).isEqualTo(createProductDTO.getDescription());
        assertThat(productSaved.getChildren()).isEqualTo(Set.of(child));
        assertThat(productSaved.getType()).isEqualTo(ProductType.PRODUCT);
        assertThat(productSaved.getGitlabGroupId()).isEqualTo(createProductDTO.getGitlabGroupId());
        assertThat(productSaved.getGitlabConfig()).isEqualTo(gitlabConfig);
        assertFalse(productSaved.getIsArchived());
    }

    @Test
    void should_find_by_name() {
        when(productRepository.findByName("Midas")).thenReturn(Optional.of(product));

        assertThat(productService.findByName("Midas")).isEqualTo(product);
    }

    @Test
    void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                productService.findByName("buffet"));
    }

    @Test
    void should_update_product_by_id() {
        UpdateProductDTO updateProductDTO = new UpdateProductDTO("oneHome", "taxable",
                user.getId(), 1L, Set.of(project.getId()), Set.of(3L), Set.of(), ProductType.PRODUCT, 451, 42L);

        when(userService.findByIdOrNull(user.getId())).thenReturn(user);
        when(gitlabConfigService.findByIdOrNull(updateProductDTO.getGitlabConfigId())).thenReturn(gitlabConfig);
        when(projectService.findById(anyLong())).thenReturn(project);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(5L, updateProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(updateProductDTO.getName());
        assertThat(productSaved.getProductManager().getId()).isEqualTo(updateProductDTO.getProductManagerId());
        assertThat(productSaved.getDescription()).isEqualTo(updateProductDTO.getDescription());
        assertThat(productSaved.getProjects()).isEqualTo(Set.of(project));
        assertThat(productSaved.getGitlabGroupId()).isEqualTo(updateProductDTO.getGitlabGroupId());
        assertThat(productSaved.getGitlabConfig()).isEqualTo(gitlabConfig);
    }

    @Test
    void should_update_is_archived_by_id() {
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
    void should_create_product_with_null_product_manager_and_null_portfolio_id() {
        CreateProductDTO createDTO = new CreateProductDTO("name", "description",
                null, null, Set.of(1L), Set.of(1L), Set.of(), ProductType.PRODUCT, null, null);

        when(productRepository.save(any())).thenReturn(product);
        doNothing().when(projectService).addProductToProjects(any(), any());

        productService.create(createDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();
        assertThat(productSaved.getProductManager()).isNull();
        assertThat(productSaved.getParent()).isNull();
    }

    @Test
    void should_update_product_with_null_product_manager_and_null_portfolio_id() {
        UpdateProductDTO updateDTO = new UpdateProductDTO("name", "description",
                null, null, Set.of(1L), Set.of(1L), Set.of(), ProductType.PRODUCT, null, null);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(5L, updateDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getProductManager()).isNull();
        assertThat(productSaved.getParent()).isNull();
    }

    @Test
    void should_addParentToChildren() {
        when(productRepository.save(any())).thenReturn(null);
        productService.addParentToChildren(parent, Set.of(product, child));

        verify(productService, times(2)).addParentToChild(any(), any());
        verify(productRepository, times(2)).save(any());
    }

}
