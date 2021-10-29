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

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductIsArchivedDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
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
    SourceControlService sourceControlService;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    UserService userService;
    @MockBean
    ProjectService projectService;
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
            .with(p -> p.setChildren(Set.of(product)))
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
        CreateProductDTO createProductDTO = new CreateProductDTO("homeOne", "new name",
                3L, 1L, Set.of(4L), Set.of(3L), Set.of(child.getId()), ProductType.PRODUCT, 454, 42L, Set.of(), "foo", "bar", "baz");

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
        assertThat(productSaved.getOwner().getId()).isEqualTo(createProductDTO.getOwnerId());
        assertThat(productSaved.getDescription()).isEqualTo(createProductDTO.getDescription());
        assertThat(productSaved.getChildren()).isEqualTo(Set.of(child));
        assertThat(productSaved.getType()).isEqualTo(ProductType.PRODUCT);
        assertThat(productSaved.getGitlabGroupId()).isEqualTo(createProductDTO.getGitlabGroupId());
        assertThat(productSaved.getSourceControl()).isEqualTo(sourceControl);
        assertThat(productSaved.getVision()).isEqualTo(createProductDTO.getVision());
        assertThat(productSaved.getMission()).isEqualTo(createProductDTO.getMission());
        assertThat(productSaved.getProblemStatement()).isEqualTo(createProductDTO.getProblemStatement());
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
                user.getId(), 1L, Set.of(project.getId()), Set.of(3L), Set.of(), ProductType.PRODUCT,
                451, 42L, Set.of(), "foo", "bar", "baz"
        );

        when(userService.findByIdOrNull(user.getId())).thenReturn(user);
        when(sourceControlService.findByIdOrNull(updateProductDTO.getSourceControlId())).thenReturn(sourceControl);
        when(projectService.findById(anyLong())).thenReturn(project);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(5L, updateProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getName()).isEqualTo(updateProductDTO.getName());
        assertThat(productSaved.getOwner().getId()).isEqualTo(updateProductDTO.getOwnerId());
        assertThat(productSaved.getDescription()).isEqualTo(updateProductDTO.getDescription());
        assertThat(productSaved.getProjects()).isEqualTo(Set.of(project));
        assertThat(productSaved.getGitlabGroupId()).isEqualTo(updateProductDTO.getGitlabGroupId());
        assertThat(productSaved.getVision()).isEqualTo(updateProductDTO.getVision());
        assertThat(productSaved.getMission()).isEqualTo(updateProductDTO.getMission());
        assertThat(productSaved.getProblemStatement()).isEqualTo(updateProductDTO.getProblemStatement());
        assertThat(productSaved.getSourceControl()).isEqualTo(sourceControl);
    }

    @Test
    void should_send_websocket_when_team_or_tag_is_updated() {
        var team = Builder.build(Team.class)
                .with(t -> t.setId(4L))
                .get();
        var tag = Builder.build(Tag.class)
                .with(t -> t.setId(6L))
                .get();
        var updateProductDTO = Builder.build(UpdateProductDTO.class)
                .with(d -> d.setTagIds(Set.of(6L)))
                .with(d -> d.setTeamIds(Set.of(4L)))
                .with(d -> d.setProjectIds(Set.of()))
                .with(d -> d.setChildIds(Set.of()))
                .get();

        when(tagService.findById(tag.getId())).thenReturn(tag);
        when(teamService.findById(team.getId())).thenReturn(team);
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(5L, updateProductDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        verify(websocket, times(1)).convertAndSend("/topic/update_team", team.toDto());

        assertThat(productCaptor.getValue().getTeams()).isEqualTo(Set.of(team));
        assertThat(productCaptor.getValue().getTags()).isEqualTo(Set.of(tag));
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
                null, null, Set.of(1L), Set.of(1L), Set.of(), ProductType.PRODUCT, null, null, Set.of(), null, null, null);

        when(productRepository.save(any())).thenReturn(product);
        doNothing().when(projectService).addProductToProjects(any(), any());

        productService.create(createDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();
        assertThat(productSaved.getOwner()).isNull();
        assertThat(productSaved.getParent()).isNull();
    }

    @Test
    void should_update_product_with_null_product_manager_and_null_portfolio_id() {
        UpdateProductDTO updateDTO = new UpdateProductDTO("name", "description",
                null, null, Set.of(1L), Set.of(1L), Set.of(), ProductType.PRODUCT, null, null, Set.of(), null, null, null);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        productService.updateById(5L, updateDTO);

        verify(productRepository, times(1)).save(productCaptor.capture());
        Product productSaved = productCaptor.getValue();

        assertThat(productSaved.getOwner()).isNull();
        assertThat(productSaved.getParent()).isNull();
    }

    @Test
    void should_addParentToChildren() {
        when(productRepository.save(any())).thenReturn(null);
        productService.addParentToChildren(parent, Set.of(product, child));

        verify(productService, times(2)).addParentToChild(any(), any());
        verify(productRepository, times(2)).save(any());
    }

    @Test
    void should_remove_inverse_relationship() {
        var team = new Team();
        team.setId(42L);
        var originalProduct = new Product();
        originalProduct.setId(1L);
        originalProduct.getTeams().add(team);
        var updatedProduct = new Product();
        updatedProduct.setTeams(Set.of());

        productService.updateTeamRemovedFromProduct(originalProduct, updatedProduct);

        verify(websocket, times(1)).convertAndSend("/topic/update_team", team.toDto());
    }

    @Test
    void should_get_all_product_ids() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertThat(productService.getAllProductIds()).isEqualTo(List.of(product.getId()));
    }
}
