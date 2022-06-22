package mil.af.abms.midas.api.portfolio;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
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

import mil.af.abms.midas.api.capability.Capability;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.dtos.SprintProductMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.issue.Issue;
import mil.af.abms.midas.api.issue.IssueService;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(PortfolioService.class)
public class PortfolioServiceTests {

    @SpyBean
    PortfolioService portfolioService;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    UserService userService;
    @MockBean
    PersonnelService personnelService;
    @MockBean
    ProductService productService;
    @MockBean
    CapabilityService capabilityService;
    @MockBean
    SourceControlService sourceControlService;
    @MockBean
    IssueService issueService;
    @MockBean
    PortfolioRepository portfolioRepository;
    @Captor
    ArgumentCaptor<Portfolio> portfolioCaptor;

    private final LocalDateTime today = LocalDateTime.now();
    private final LocalDate currentDate = LocalDate.now();
    private final User user = Builder.build(User.class).with(u -> u.setId(1L)).get();
    private final User user2 = Builder.build(User.class).with(u -> u.setId(10L)).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(2L))
            .with(p -> p.setName("MIDAS"))
            .get();
    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(42L))
            .with(g -> g.setName("Mock IL2"))
            .get();
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(3L))
            .with(p -> p.setPersonnel(new Personnel()))
            .with(p -> p.setName("ABMS"))
            .with(p -> p.setGanttNote("Gantt Note"))
            .with(p -> p.setGanttNoteModifiedAt(today))
            .with(p -> p.setGanttNoteModifiedBy(user))
            .with(p -> p.setSprintStartDate(currentDate))
            .with(p -> p.setSprintDurationInDays(7))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setProducts(Set.of(product)))
            .get();
    private final Personnel personnel = Builder.build(Personnel.class)
            .with(p -> p.setId(4L))
            .with(p -> p.setOwner(user))
            .with(p -> p.setAdmins(Set.of()))
            .with(p -> p.setPortfolios(portfolio))
            .with(p -> p.setProduct(null))
            .with(p -> p.setTeams(Set.of()))
            .get();
    private final Capability capability = Builder.build(Capability.class)
            .with(c -> c.setId(5L))
            .with(c -> c.setTitle("title"))
            .with(c -> c.setReferenceId(0))
            .get();
    private final CreatePortfolioDTO createPortfolioDTO = Builder.build(CreatePortfolioDTO.class)
            .with(d -> d.setName("ABMS"))
            .with(d -> d.setProductIds(Set.of(2L)))
            .with(d -> d.setCapabilityIds(Set.of(5L)))
            .with(p -> p.setGitlabGroupId(123))
            .with(p -> p.setSourceControlId(42L))
            .get();
    private final Issue issue = Builder.build(Issue.class)
            .with(i -> i.setId(6L))
            .with(i -> i.setWeight(5L))
            .with(i -> i.setCompletedAt(LocalDate.parse("2022-06-17").atStartOfDay()))
            .get();

    @Test
    void should_create_portfolio_no_personnel() {
        when(personnelService.create(any(CreatePersonnelDTO.class))).thenReturn(new Personnel());
        when(productService.findById(anyLong())).thenReturn(product);
        when(capabilityService.findById(anyLong())).thenReturn(capability);

        portfolioService.create(createPortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getPersonnel()).isEqualTo(new Personnel());
        assertThat(portfolioSaved.getProducts()).isEqualTo(Set.of(product));
        assertThat(portfolioSaved.getName()).isEqualTo(createPortfolioDTO.getName());
        assertThat(portfolioSaved.getDescription()).isEqualTo(createPortfolioDTO.getDescription());
        assertThat(portfolioSaved.getGitlabGroupId()).isEqualTo(createPortfolioDTO.getGitlabGroupId());
        assertThat(portfolioSaved.getVision()).isEqualTo(createPortfolioDTO.getVision());
        assertThat(portfolioSaved.getMission()).isEqualTo(createPortfolioDTO.getMission());
        assertThat(portfolioSaved.getProblemStatement()).isEqualTo(createPortfolioDTO.getProblemStatement());
        assertThat(portfolioSaved.getSourceControl()).isEqualTo(null);
        assertThat(portfolioSaved.getCapabilities()).isEqualTo(Set.of(capability));
        assertThat(portfolioSaved.getSprintStartDate()).isEqualTo(currentDate);
        assertThat(portfolioSaved.getSprintDurationInDays()).isEqualTo(7);

    }
    @Test
    void should_create_portfolio_with_personnel() {
        LocalDate newDate = currentDate.plusDays(1);
        CreatePersonnelDTO createPersonnelDTO = Builder.build(CreatePersonnelDTO.class)
                .with(d -> d.setOwnerId(1L))
                .get();
        CreatePortfolioDTO createPortfolioDTO = Builder.build(CreatePortfolioDTO.class)
                .with(d -> d.setName("ABMS"))
                .with(d -> d.setPersonnel(createPersonnelDTO))
                .with(d -> d.setSprintStartDate(newDate))
                .with(d -> d.setSprintDurationInDays(14))
                .get();
        portfolio.setPersonnel(personnel);

        when(personnelService.create(any(CreatePersonnelDTO.class))).thenReturn(personnel);
        when(productService.findById(anyLong())).thenReturn(product);

        portfolioService.create(createPortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getPersonnel()).isEqualTo(personnel);
        assertThat(portfolioSaved.getSprintStartDate()).isEqualTo(newDate);
        assertThat(portfolioSaved.getSprintDurationInDays()).isEqualTo(14);
    }

    @Test
    void should_update_portfolio_by_id() {
        LocalDate newDate = currentDate.plusDays(3);
        UpdatePersonnelDTO updatePersonnelDTO = Builder.build(UpdatePersonnelDTO.class)
                .with(d -> d.setOwnerId(10L))
                .get();
        UpdatePortfolioDTO updatePortfolioDTO = Builder.build(UpdatePortfolioDTO.class)
                .with(p -> p.setName("new name"))
                .with(p -> p.setDescription("new description"))
                .with(p -> p.setPersonnel(updatePersonnelDTO))
                .with(p -> p.setProductIds(Set.of(4L)))
                .with(p -> p.setCapabilityIds(Set.of(5L)))
                .with(p -> p.setGanttNote("Test Gantt Note"))
                .with(d -> d.setSprintStartDate(newDate))
                .with(d -> d.setSprintDurationInDays(28))
                .get();
        personnel.setOwner(user2);
        portfolio.setPersonnel(personnel);

        doReturn(portfolio).when(portfolioService).findById(anyLong());
        when(productService.findById(anyLong())).thenReturn(product);
        when(capabilityService.findById(anyLong())).thenReturn(capability);
        when(personnelService.updateById(anyLong(), any(UpdatePersonnelDTO.class))).thenReturn(personnel);
        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        portfolioService.updateById(3L, updatePortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getName()).isEqualTo(updatePortfolioDTO.getName());
        assertThat(portfolioSaved.getDescription()).isEqualTo(updatePortfolioDTO.getDescription());
        assertThat(portfolioSaved.getPersonnel()).isEqualTo(personnel);
        assertThat(portfolioSaved.getProducts()).isEqualTo(Set.of(product));
        assertThat(portfolioSaved.getVision()).isEqualTo(updatePortfolioDTO.getVision());
        assertThat(portfolioSaved.getMission()).isEqualTo(updatePortfolioDTO.getMission());
        assertThat(portfolioSaved.getProblemStatement()).isEqualTo(updatePortfolioDTO.getProblemStatement());
        assertThat(portfolioSaved.getCapabilities()).isEqualTo((Set.of(capability)));
        assertThat(portfolioSaved.getGanttNote()).isEqualTo(updatePortfolioDTO.getGanttNote());
        assertThat(portfolioSaved.getSprintStartDate()).isEqualTo(updatePortfolioDTO.getSprintStartDate());
        assertThat(portfolioSaved.getSprintDurationInDays()).isEqualTo(updatePortfolioDTO.getSprintDurationInDays());
    }

    @Test
    void should_update_portfolio_by_id_without_gantt_note_change() {
        UpdatePersonnelDTO updatePersonnelDTO = Builder.build(UpdatePersonnelDTO.class)
                .with(d -> d.setOwnerId(10L))
                .get();
        UpdatePortfolioDTO updatePortfolioDTO = Builder.build(UpdatePortfolioDTO.class)
                .with(p -> p.setName("new name"))
                .with(p -> p.setDescription("new description"))
                .with(p -> p.setPersonnel(updatePersonnelDTO))
                .with(p -> p.setProductIds(Set.of(4L)))
                .with(p -> p.setCapabilityIds(Set.of(5L)))
                .with(p -> p.setGanttNote("Gantt Note"))
                .get();
        personnel.setOwner(user2);
        portfolio.setPersonnel(personnel);

        doReturn(portfolio).when(portfolioService).findById(anyLong());
        when(productService.findById(anyLong())).thenReturn(product);
        when(capabilityService.findById(anyLong())).thenReturn(capability);
        when(personnelService.updateById(anyLong(), any(UpdatePersonnelDTO.class))).thenReturn(personnel);
        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolio));
        when(portfolioRepository.save(portfolio)).thenReturn(portfolio);

        portfolioService.updateById(3L, updatePortfolioDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertThat(portfolioSaved.getGanttNote()).isEqualTo("Gantt Note");
    }

    @Test
    void should_update_is_archived_by_id() {
        Portfolio portfolioWithProduct = new Portfolio();
        BeanUtils.copyProperties(portfolio, portfolioWithProduct);
        portfolioWithProduct.setProducts(Set.of(product));

        IsArchivedDTO updateDTO = Builder.build(IsArchivedDTO.class).with(d -> d.setIsArchived(true)).get();

        when(portfolioRepository.findById(anyLong())).thenReturn(Optional.of(portfolioWithProduct));
        when(portfolioRepository.save(any())).thenReturn(portfolioWithProduct);
        when(productService.updateIsArchivedById(any(), any())).thenReturn(product);

        portfolioService.updateIsArchivedById(5L, updateDTO);

        verify(portfolioRepository, times(1)).save(portfolioCaptor.capture());
        Portfolio portfolioSaved = portfolioCaptor.getValue();

        assertTrue(portfolioSaved.getIsArchived());
    }

    @Test
    void should_find_by_name() {
        when(portfolioRepository.findByName("ABMS")).thenReturn(Optional.of(portfolio));

        assertThat(portfolioService.findByName("ABMS")).isEqualTo(portfolio);
    }

    @Test
    void should_throw_error_find_by_name() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () -> portfolioService.findByName("buffet"));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "ABMS : 5 : 6 : true",
            "foo : 5 : 42 : true",
            "foo : 123 : 42 : false",
            "foo : 123 : 6 : true"
    }, delimiter = ':')
    void should_validate_Unique_Source_Control_And_Gitlab_Group(String name, Integer gitlabId, Long sourceControlId, boolean expected) {
        doReturn(List.of(portfolio)).when(portfolioService).getAll();
        createPortfolioDTO.setName(name);
        createPortfolioDTO.setGitlabGroupId(gitlabId);
        createPortfolioDTO.setSourceControlId(sourceControlId);

        assertThat(portfolioService.validateUniqueSourceControlAndGitlabGroup(createPortfolioDTO)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {
            " : true",
            "123 : false",
    }, delimiter = ':')
    void should_validate_Unique_Source_Control_And_Gitlab_Group_For_Null_Filters(Integer gitlabGroupId, boolean setSourceControl) {
        Portfolio newPortfolio = new Portfolio();
        newPortfolio.setName("foo");
        newPortfolio.setGitlabGroupId(gitlabGroupId);
        newPortfolio.setSourceControl(setSourceControl ? sourceControl : null);
        doReturn(List.of(newPortfolio)).when(portfolioService).getAll();

        assertTrue(portfolioService.validateUniqueSourceControlAndGitlabGroup(createPortfolioDTO));
    }

    @Test
    void should_get_sprint_metrics() {
        SprintProductMetricsDTO dto1 = new SprintProductMetricsDTO(LocalDate.parse("2022-06-02"), 5L, 1);
        SprintProductMetricsDTO dto2 = new SprintProductMetricsDTO(LocalDate.parse("2022-06-16"), 5L, 1);
        HashMap<Long, List<SprintProductMetricsDTO>> metricsMap = new HashMap<>();
        metricsMap.put(2L, List.of(dto1, dto2));

        Issue issueNotCompleted = new Issue();
        BeanUtils.copyProperties(issue, issueNotCompleted);
        issueNotCompleted.setCompletedAt(null);

        Issue issueBeforeDate = new Issue();
        BeanUtils.copyProperties(issue, issueBeforeDate);
        issueBeforeDate.setCompletedAt(LocalDate.parse("2022-06-02").atStartOfDay());

        doReturn(portfolio).when(portfolioService).findById(anyLong());
        when(issueService.getAllIssuesByProductId(anyLong())).thenReturn(List.of(issue, issueNotCompleted, issueBeforeDate));
        doReturn(List.of(dto1, dto2)).when(productService).getSprintMetrics(anyLong(), any(), anyInt(), anyInt());

        assertThat(portfolioService.getSprintMetrics(91L, LocalDate.parse("2022-06-16"), 14, 2)).isEqualTo(metricsMap);
    }
}
