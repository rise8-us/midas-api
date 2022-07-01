package mil.af.abms.midas.api.product;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.dtos.SprintProductMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.issue.Issue;
import mil.af.abms.midas.api.issue.IssueService;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.release.Release;
import mil.af.abms.midas.api.release.ReleaseService;
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
    ReleaseService releaseService;
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
    IssueService issueService;
    @MockBean
    ProductRepository productRepository;
    @Captor
    ArgumentCaptor<Product> productCaptor;

    private final User user = Builder.build(User.class)
            .with(u -> u.setId(3L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("Lambo"))
            .get();
    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(42L))
            .with(g -> g.setName("Mock IL2"))
            .get();
    private final Release release = Builder.build(Release.class)
            .with(r -> r.setId(100L))
            .with(r -> r.setReleasedAt(LocalDateTime.parse("2022-06-10T12:00:00")))
            .get();
    private final Project project = Builder.build(Project.class)
            .with(p -> p.setId(4L))
            .with(p -> p.setName("backend"))
            .with(p -> p.setReleases(Set.of(release)))
            .get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(5L))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setProjects(Set.of(project)))
            .get();
    private final Product parent = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Metrics"))
            .get();
    private final Product child = Builder.build(Product.class)
            .with(p -> p.setId(6L))
            .with(p -> p.setName("Metrics"))
            .get();
    private final CreateProductDTO createProductDTO = Builder.build(CreateProductDTO.class)
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("description"))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setSourceControlId(42L))
            .get();
    private final Issue issue = Builder.build(Issue.class)
            .with(i -> i.setId(6L))
            .with(i -> i.setWeight(5L))
            .with(i -> i.setCompletedAt(LocalDate.parse("2022-06-17").atStartOfDay()))
            .get();

    @Test
    void should_create_product() {
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
    void should_get_all_products() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertThat(productService.getAll()).isEqualTo(List.of(product));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "Midas : 5 : 6 : true",
            "foo : 5 : 42 : true",
            "foo : 123 : 42 : false",
            "foo : 123 : 6 : true"
    }, delimiter = ':')
    void should_validate_Unique_Source_Control_And_Gitlab_Group(String name, Integer gitlabId, Long sourceControlId, boolean expected) {
        doReturn(List.of(product)).when(productService).getAll();
        createProductDTO.setName(name);
        createProductDTO.setGitlabGroupId(gitlabId);
        createProductDTO.setSourceControlId(sourceControlId);

        assertThat(productService.validateUniqueSourceControlAndGitlabGroup(createProductDTO)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {
            " : true",
            "123 : false",
    }, delimiter = ':')
    void should_validate_Unique_Source_Control_And_Gitlab_Group_For_Null_Filters(Integer gitlabGroupId, boolean setSourceControl) {
        Product newProduct = new Product();
        newProduct.setName("foo");
        newProduct.setGitlabGroupId(gitlabGroupId);
        newProduct.setSourceControl(setSourceControl ? sourceControl : null);
        doReturn(List.of(newProduct)).when(productService).getAll();

        assertTrue(productService.validateUniqueSourceControlAndGitlabGroup(createProductDTO));
    }

    @Test
    void should_get_sprint_metrics() {
        SprintProductMetricsDTO dto1 = Builder.build(SprintProductMetricsDTO.class)
                .with(d -> d.setDate(LocalDate.parse("2022-06-16")))
                .with(d -> d.setDeliveredPoints(5L))
                .with(d -> d.setDeliveredStories(1))
                .with(d -> d.setReleaseFrequency(0.0F))
                .with(d -> d.setLeadTimeForChangeInMinutes(0.0F))
                .get();
        SprintProductMetricsDTO dto2 = Builder.build(SprintProductMetricsDTO.class)
                .with(d -> d.setDate(LocalDate.parse("2022-06-02")))
                .with(d -> d.setDeliveredPoints(5L))
                .with(d -> d.setDeliveredStories(1))
                .with(d -> d.setReleaseFrequency(1 / 14F))
                .with(d -> d.setLeadTimeForChangeInMinutes(0.0F))
                .get();

        Issue issueNotCompleted = new Issue();
        BeanUtils.copyProperties(issue, issueNotCompleted);
        issueNotCompleted.setCompletedAt(null);

        Issue issueBeforeDate = new Issue();
        BeanUtils.copyProperties(issue, issueBeforeDate);
        issueBeforeDate.setCompletedAt(LocalDate.parse("2022-06-02").atStartOfDay());

        doReturn(product).when(productService).findById(anyLong());
        doReturn(0F).when(productService).calculateLeadTimeForChange(anySet());
        when(issueService.getAllIssuesByProductId(anyLong())).thenReturn(List.of(issue, issueNotCompleted, issueBeforeDate));

        assertThat(productService.getSprintMetrics(91L, LocalDate.parse("2022-06-16"), 14, 2)).isEqualTo(List.of(dto1, dto2));
    }

    @Test
    void should_get_current_sprint_metrics_into_future() {
        Release r2 = new Release();
        r2.setId(100L);
        r2.setReleasedAt(LocalDateTime.now().minusDays(2));

        Project project2 = new Project();
        BeanUtils.copyProperties(project, project2);
        project2.setReleases(Set.of(r2));

        Product product2 = new Product();
        BeanUtils.copyProperties(product, product2);
        product2.setProjects(Set.of(project2));

        doReturn(product).when(productService).findById(anyLong());
        doReturn(0F).when(productService).calculateLeadTimeForChange(anySet());
        when(issueService.getAllIssuesByProductId(anyLong())).thenReturn(List.of());
        LocalDate todayMinusFive = LocalDate.now().minusDays(5L);
        SprintProductMetricsDTO dto = productService.populateProductMetrics(todayMinusFive, product2, 14);

        assertThat(dto.getReleaseFrequency()).isEqualTo(1 / 5F);
    }

    @Test
    void should_calculate_lead_time_for_change() {
        Release release2 = new Release();
        BeanUtils.copyProperties(release, release2);
        release2.setProject(project);
        release2.setReleasedAt(LocalDateTime.parse("2022-06-18T12:00:00"));

        Release previousRelease = new Release();
        BeanUtils.copyProperties(release, previousRelease);
        previousRelease.setProject(project);
        previousRelease.setReleasedAt(LocalDateTime.parse("2022-01-01T12:00:00"));

        doReturn(Optional.of(previousRelease)).when(releaseService).getPreviousReleaseByProjectIdAndReleasedAt(any(), any());
        doReturn(List.of(issue)).when(issueService).findAllIssuesByProjectIdAndCompletedAtDateRange(any(), any(), any());

        float leadTime = productService.calculateLeadTimeForChange(Set.of(release2));

        assertThat(leadTime).isEqualTo(2160F);
    }

    @Test
    void should_handle_no_previous_release_for_calculate_lead_time_for_change() {
        Release release2 = new Release();
        BeanUtils.copyProperties(release, release2);
        release2.setProject(project);
        release2.setReleasedAt(LocalDateTime.parse("2022-06-18T12:00:00"));

        doReturn(Optional.empty()).when(releaseService).getPreviousReleaseByProjectIdAndReleasedAt(any(), any());
        doReturn(List.of(issue)).when(issueService).findAllIssuesByProjectIdAndCompletedAtDateRange(any(), any(), any());

        float leadTime = productService.calculateLeadTimeForChange(Set.of(release2));

        assertThat(leadTime).isEqualTo(2160F);
    }

    @Test
    void should_calculate_lead_time_for_change_for_no_issues_or_no_releases() {
        float leadTime = productService.calculateLeadTimeForChange(Set.of());

        assertThat(leadTime).isEqualTo(-1F);
    }
}
