package mil.af.abms.midas.api.completion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.completion.dto.CreateCompletionDTO;
import mil.af.abms.midas.api.completion.dto.UpdateCompletionDTO;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.issue.Issue;
import mil.af.abms.midas.api.issue.IssueService;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.enums.CompletionType;

@ExtendWith(SpringExtension.class)
@Import(CompletionService.class)
class CompletionServiceTests {

    @SpyBean
    CompletionService completionService;
    @MockBean
    EpicService epicService;
    @MockBean
    IssueService issueService;
    @MockBean
    CompletionRepository repository;
    @Captor
    ArgumentCaptor<Completion> completionCaptor;
    @MockBean
    SimpMessageSendingOperations websocket;

    private final Completion completion = Builder.build(Completion.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setValue(0F))
            .with(c -> c.setTarget(1F))
            .with(c -> c.setCompletionType(CompletionType.BINARY))
            .with(c -> c.setDueDate(null))
            .with(c -> c.setStartDate(null))
            .with(c -> c.setCompletedAt(null))
            .get();
    private final Product product = Builder.build(Product.class)
            .with(c -> c.setId(1L))
            .get();
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(c -> c.setId(1L))
            .get();
    private final Epic epicWithProduct = Builder.build(Epic.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedWeight(2L))
            .with(e -> e.setTotalWeight(5L))
            .with(e -> e.setCompletedAt(LocalDateTime.now()))
            .with(e -> e.setStartDate(LocalDate.now().minusDays(1)))
            .with(e -> e.setDueDate(LocalDate.now().plusDays(1)))
            .with(e -> e.setCompletions(Set.of(completion)))
            .with(e -> e.setProduct(product))
            .get();
    private final Epic epicWithPortfolio = Builder.build(Epic.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedWeight(2L))
            .with(e -> e.setTotalWeight(5L))
            .with(e -> e.setCompletedAt(LocalDateTime.now()))
            .with(e -> e.setStartDate(LocalDate.now().minusDays(1)))
            .with(e -> e.setDueDate(LocalDate.now().plusDays(1)))
            .with(e -> e.setCompletions(Set.of(completion)))
            .with(e -> e.setPortfolio(portfolio))
            .get();
    private final Issue issue = Builder.build(Issue.class)
            .with(i -> i.setId(1L))
            .with(i -> i.setTitle("issue"))
            .with(i -> i.setState("closed"))
            .with(i -> i.setWeight(1L))
            .with(i -> i.setCompletedAt(LocalDateTime.now()))
            .with(i -> i.setStartDate(LocalDate.now().minusDays(1)))
            .with(i -> i.setDueDate(LocalDate.now().plusDays(1)))
            .with(i -> i.setCompletions(Set.of(completion)))
            .get();

    Completion completion2 = new Completion();
    Issue issue2 = new Issue();

    @BeforeEach
    void init() {
        BeanUtils.copyProperties(completion, completion2);
        BeanUtils.copyProperties(issue, issue2);
    }

    @Test
    void should_create_completion() {
        CreateCompletionDTO createCompletionDTO = new CreateCompletionDTO(null, null, null, null, null, null, 1L, null);

        doReturn(null).when(epicService).findByIdOrNull(anyLong());
        doReturn(null).when(issueService).findByIdOrNull(anyLong());

        completionService.create(createCompletionDTO);
        verify(repository, times(1)).save(completionCaptor.capture());
        Completion completionSaved = completionCaptor.getValue();

        assertThat(completionSaved.getValue()).isEqualTo(0F);
        assertThat(completionSaved.getTarget()).isEqualTo(1F);
        assertThat(completionSaved.getStartDate()).isNull();
        assertThat(completionSaved.getDueDate()).isNull();
        assertThat(completionSaved.getCompletedAt()).isNull();
        assertThat(completionSaved.getEpic()).isNull();
        assertThat(completionSaved.getIssue()).isNull();
    }

    @ParameterizedTest
    @CsvSource(value = {"0.0 : 1.0", "5.0 : 5.0"}, delimiter = ':')
    void should_update_completion_by_id(Float value, Float target) {
        UpdateCompletionDTO updateCompletionDTO = new UpdateCompletionDTO(
                null,
                null,
                CompletionType.BINARY,
                value,
                target,
                null,
                null
        );
        doReturn(completion).when(completionService).findById(anyLong());

        completionService.updateById(1L, updateCompletionDTO);

        verify(repository, times(1)).save(completionCaptor.capture());
        Completion completionSaved = completionCaptor.getValue();

        assertThat(completionSaved.getValue()).isEqualTo(value);
        assertThat(completionSaved.getTarget()).isEqualTo(target);
    }

    @Test
    void should_set_completion_start_date() {
        LocalDate date = LocalDate.now();

        doReturn(completion).when(completionService).findById(anyLong());

        completionService.setStartDate(completion.getId(), date);

        verify(repository, times(1)).save(completionCaptor.capture());
        Completion completionSaved = completionCaptor.getValue();

        assertThat(completionSaved.getStartDate()).isEqualTo(date);
    }

    @Test
    void should_set_completion_completed_at_and_value_to_target() {
        doReturn(completion).when(completionService).findById(anyLong());

        completionService.setCompletedAtAndValueToTarget(completion.getId());

        verify(repository, times(1)).save(completionCaptor.capture());
        Completion completionSaved = completionCaptor.getValue();

        assertThat(completionSaved.getValue()).isEqualTo(completionSaved.getTarget());
        assertThat(completionSaved.getCompletedAt()).isNotNull();
    }

    @Test
    void should_set_completion_type_to_failure() {
        doReturn(completion).when(completionService).findById(anyLong());

        completionService.setCompletionTypeToFailure(completion.getId());

        verify(repository, times(1)).save(completionCaptor.capture());
        Completion completionSaved = completionCaptor.getValue();

        assertThat(completionSaved.getCompletionType()).isEqualTo(CompletionType.CONNECTION_FAILURE);
    }

    @Test
    void should_linkGitlabEpic_for_product() {
        doReturn(epicWithProduct).when(epicService).findByIdOrNull(anyLong());
        doReturn(epicWithProduct).when(epicService).updateByIdForProduct(anyLong());

        completionService.linkGitlabEpic(1L, completion2);

        assertThat(completion2.getEpic()).isEqualTo(epicWithProduct);
        assertThat(completion2.getStartDate()).isEqualTo(epicWithProduct.getStartDate());
        assertThat(completion2.getDueDate()).isEqualTo(epicWithProduct.getDueDate());
        assertThat(completion2.getCompletedAt()).isEqualTo(epicWithProduct.getCompletedAt());
    }

    @Test
    void should_linkGitlabEpic_for_portfolio() {
        doReturn(epicWithPortfolio).when(epicService).findByIdOrNull(anyLong());
        doReturn(epicWithPortfolio).when(epicService).updateByIdForPortfolio(anyLong());

        completionService.linkGitlabEpic(1L, completion2);

        assertThat(completion2.getEpic()).isEqualTo(epicWithPortfolio);
        assertThat(completion2.getStartDate()).isEqualTo(epicWithPortfolio.getStartDate());
        assertThat(completion2.getDueDate()).isEqualTo(epicWithPortfolio.getDueDate());
        assertThat(completion2.getCompletedAt()).isEqualTo(epicWithPortfolio.getCompletedAt());
    }

    @Test
    void should_linkGitlabIssue_for_completed_issues() {
        doReturn(issue).when(issueService).findByIdOrNull(anyLong());
        doReturn(issue).when(issueService).updateById(anyLong());

        completionService.linkGitlabIssue(1L, completion2);

        assertThat(completion2.getIssue()).isEqualTo(issue);
        assertThat(completion2.getValue()).isEqualTo(issue.getWeight().floatValue());
        assertThat(completion2.getTarget()).isEqualTo(issue.getWeight().floatValue());
        assertThat(completion2.getStartDate()).isEqualTo(issue.getStartDate());
        assertThat(completion2.getDueDate()).isEqualTo(issue.getDueDate());
        assertThat(completion2.getCompletedAt()).isEqualTo(issue.getCompletedAt());
    }

    @Test
    void should_linkGitlabIssue_for_in_progress_issues() {
        issue2.setCompletedAt(null);

        doReturn(issue2).when(issueService).findByIdOrNull(anyLong());
        doReturn(issue2).when(issueService).updateById(anyLong());

        completionService.linkGitlabIssue(1L, completion2);

        assertThat(completion2.getCompletedAt()).isNull();
        assertThat(completion2.getValue()).isEqualTo(0F);
    }

    @Test
    void should_update_linked_issue() {
        doNothing().when(completionService).updateCompletionWithGitlabIssue(any(), any());

        completionService.updateLinkedIssue(issue);

        verify(completionService, times(1)).updateLinkedIssue(any());
    }
}
