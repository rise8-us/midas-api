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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(ProductService.class)
class ProductServiceTests {

    @SpyBean
    ProductService productService;
    @MockBean
    SourceControlService sourceControlService;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    UserService userService;
    @MockBean
    ProjectService projectService;
    @MockBean
    PortfolioService portfolioService;
    @MockBean
    PersonnelService personnelService;
    @MockBean
    TagService tagService;
    @MockBean
    TeamService teamService;
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
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setName("Midas")).get();
    private final Product parent = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Metrics")).get();
    private final Product child = Builder.build(Product.class)
            .with(p -> p.setId(6L))
            .with(p -> p.setName("Metrics")).get();
    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(42L))
            .with(g -> g.setName("Mock IL2"))
            .get();

    @Test
    void should_create_product() {
        CreateProductDTO createProductDTO = Builder.build(CreateProductDTO.class)
                .with(p -> p.setName("name"))
                .with(p -> p.setDescription("description"))
                .get();

        when(sourceControlService.findByIdOrNull(createProductDTO.getSourceControlId())).thenReturn(sourceControl);
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
        assertThat(productSaved.getDescription()).isEqualTo(createProductDTO.getDescription());
        assertThat(productSaved.getGitlabGroupId()).isEqualTo(createProductDTO.getGitlabGroupId());
        assertThat(productSaved.getSourceControl()).isEqualTo(sourceControl);
        assertThat(productSaved.getVision()).isEqualTo(createProductDTO.getVision());
        assertThat(productSaved.getMission()).isEqualTo(createProductDTO.getMission());
        assertThat(productSaved.getProblemStatement()).isEqualTo(createProductDTO.getProblemStatement());
        assertThat(productSaved.getRoadmapType()).isEqualTo(createProductDTO.getRoadmapType());
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
        UpdateProductDTO updateProductDTO = Builder.build(UpdateProductDTO.class)
                .with(p -> p.setName("new name"))
                .with(p -> p.setDescription("new description"))
                .with(p -> p.setProjectIds(Set.of(4L)))
                .get();

        when(userService.findByIdOrNull(user.getId())).thenReturn(user);
        when(sourceControlService.findByIdOrNull(updateProductDTO.getSourceControlId())).thenReturn(sourceControl);
        when(projectService.findById(anyLong())).thenReturn(project);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(5L, updateProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(updateProductDTO.getName());
        assertThat(productSaved.getDescription()).isEqualTo(updateProductDTO.getDescription());
        assertThat(productSaved.getProjects()).isEqualTo(Set.of(project));
        assertThat(productSaved.getGitlabGroupId()).isEqualTo(updateProductDTO.getGitlabGroupId());
        assertThat(productSaved.getVision()).isEqualTo(updateProductDTO.getVision());
        assertThat(productSaved.getMission()).isEqualTo(updateProductDTO.getMission());
        assertThat(productSaved.getProblemStatement()).isEqualTo(updateProductDTO.getProblemStatement());
        assertThat(productSaved.getSourceControl()).isEqualTo(sourceControl);
        assertThat(productSaved.getRoadmapType()).isEqualTo(updateProductDTO.getRoadmapType());
    }

    @Test
    void should_update_is_archived_by_id() {
        Product productWithProject = new Product();
        BeanUtils.copyProperties(product, productWithProject);
        productWithProject.setProjects(Set.of(project));

        IsArchivedDTO updateDTO = Builder.build(IsArchivedDTO.class)
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
    void should_get_all_product_ids() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertThat(productService.getAllProductIds()).isEqualTo(List.of(product.getId()));
    }
}
