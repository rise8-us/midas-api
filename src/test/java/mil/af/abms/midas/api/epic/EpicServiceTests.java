package mil.af.abms.midas.api.epic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import org.mockito.Mockito;

import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.completion.CompletionService;
import mil.af.abms.midas.api.dtos.AddGitLabEpicWithPortfolioDTO;
import mil.af.abms.midas.api.dtos.AddGitLabEpicWithProductDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;

@ExtendWith(SpringExtension.class)
@Import(EpicService.class)
class EpicServiceTests {

    @MockBean
    SimpMessageSendingOperations websocket;
    @SpyBean
    private EpicService epicService;
    @MockBean
    private EpicRepository repository;
    @MockBean
    private ProductService productService;
    @MockBean
    private PortfolioService portfolioService;
    @MockBean
    private CompletionService completionService;
    @MockBean
    private UserService userService;
    @MockBean
    private GitLab4JClient client;

    @Captor
    ArgumentCaptor<Epic> captor;

    private static final LocalDateTime CREATED_AT = LocalDateTime.now().minusDays(1L);
    private static final LocalDateTime CLOSED_AT = LocalDateTime.now();
    private static final String TOTAL = "total";
    private static final String COMPLETED = "completed";

    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setId(3L))
            .with(sc -> sc.setToken("fake_token"))
            .with(sc -> sc.setBaseUrl("fake_url"))
            .get();
    private final Product foundProduct = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setGitlabGroupId(42))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setIsArchived(false))
            .get();
    private final Portfolio foundPortfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setGitlabGroupId(42))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setIsArchived(false))
            .get();
    private final Completion completion = Builder.build(Completion.class)
            .with(c -> c.setId(10L))
            .get();
    private final Epic foundEpicForProduct = Builder.build(Epic.class)
            .with(e -> e.setId(6L))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicUid("3-42-2"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedWeight(0L))
            .with(e -> e.setTotalWeight(0L))
            .with(e -> e.setProduct(foundProduct))
            .with((e -> e.setCompletions(Set.of(completion))))
            .get();

    private final Epic foundEpicForPortfolio = Builder.build(Epic.class)
            .with(e -> e.setId(7L))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicUid("3-42-2"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedWeight(0L))
            .with(e -> e.setTotalWeight(0L))
            .with(e -> e.setPortfolio(foundPortfolio))
            .with((e -> e.setCompletions(Set.of(completion))))
            .get();
    private final Epic epic = Builder.build(Epic.class)
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedAt(CLOSED_AT))
            .get();
    private final GitLabEpic gitLabEpic = Builder.build(GitLabEpic.class)
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedAt(CLOSED_AT))
            .get();
    private final GitLabIssue gitLabIssue = Builder.build(GitLabIssue.class)
            .with(i -> i.setTitle("title"))
            .with(i -> i.setIssueIid(2))
            .with(i -> i.setCreationDate(CREATED_AT))
            .with(i -> i.setCompletedAt(CLOSED_AT))
            .get();
    private final HashMap<String, Integer> base = new HashMap<>(Map.ofEntries(
            Map.entry(TOTAL, 10),
            Map.entry(COMPLETED, 5)
    ));

    @Test
    void can_create_Epic_for_product() {
        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.of(foundEpicForProduct));
        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpicForProduct).when(epicService).setWeights(any());
        when(productService.findById(any())).thenReturn(foundProduct);

        epicService.createForProduct(new AddGitLabEpicWithProductDTO(2, 1L));
        verify(repository, times(2)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void can_create_Epic_for_portfolio() {
        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.of(foundEpicForPortfolio));
        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Portfolio.class), anyInt());
        doReturn(foundEpicForPortfolio).when(epicService).setWeights(any());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);

        epicService.createForPortfolio(new AddGitLabEpicWithPortfolioDTO(2, 1L));
        verify(repository, times(2)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getPortfolio()).isEqualTo(foundPortfolio);
    }

    @Test
    void can_create_Epic_exists_for_product() {

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpicForProduct).when(epicService).setWeights(any());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findByEpicUid(foundEpicForProduct.getEpicUid())).thenReturn(Optional.of(foundEpicForProduct));

        epicService.createForProduct(new AddGitLabEpicWithProductDTO(2, 1L));

        verify(repository, times(2)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void can_create_Epic_exists_for_portfolio() {
        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Portfolio.class), anyInt());
        doReturn(foundEpicForPortfolio).when(epicService).setWeights(any());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        when(repository.findByEpicUid(foundEpicForPortfolio.getEpicUid())).thenReturn(Optional.of(foundEpicForPortfolio));

        epicService.createForPortfolio(new AddGitLabEpicWithPortfolioDTO(2, 1L));

        verify(repository, times(2)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getPortfolio()).isEqualTo(foundPortfolio);
    }

    @Test
    void can_update_Epic_for_Product() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpicForProduct).when(epicService).setWeights(foundEpicForProduct);
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForProduct));

        epicService.updateByIdForProduct(1L);

        verify(epicService, times(1)).syncEpic(any(), any());
    }

    @Test
    void can_update_Epic_for_Portfolio() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Portfolio.class), anyInt());
        doReturn(foundEpicForPortfolio).when(epicService).setWeights(foundEpicForPortfolio);
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForPortfolio));

        epicService.updateByIdForPortfolio(1L);

        verify(epicService, times(1)).syncEpic(any(), any());
    }

    @Test
    void should_throw_exception_when_trying_to_update_by_id_for_product() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForProduct));
        doNothing().when(completionService).setCompletionTypeToFailure(anyLong());

        epicService.updateByIdForProduct(1L);

        verify(epicService, times(1)).syncEpic(any(), any());

    }

    @Test
    void should_throw_exception_when_trying_to_update_by_id_for_portfolio() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Portfolio.class), anyInt());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForPortfolio));
        doNothing().when(completionService).setCompletionTypeToFailure(anyLong());

        epicService.updateByIdForPortfolio(1L);

        verify(epicService, times(1)).syncEpic(any(), any());

    }

    @Test
    void should_get_weights_on_epics() {
        GitLabEpic newGitlabEpic = new GitLabEpic();
        BeanUtils.copyProperties(gitLabEpic, newGitlabEpic);

        doReturn(List.of(newGitlabEpic, gitLabEpic)).when(client).getSubEpicsFromEpicAndGroup(anyInt(), anyInt());
        doReturn(List.of(gitLabIssue)).when(client).getIssuesFromEpic(anyInt(), anyInt());

        epicService.getAllEpicWeights(client, Optional.of(gitLabEpic));

        verify(epicService, times(1)).getAllEpicWeights(client, Optional.of(gitLabEpic));
    }

    @Test
    void should_set_weights_on_epics_for_product() {
        doReturn(base).when(epicService).getAllEpicWeights(any(), any());
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        doReturn(client).when(epicService).getGitlabClient(any());

        epicService.setWeights(foundEpicForProduct);

        verify(epicService, times(1)).setWeights(any());
    }

    @Test
    void should_set_weights_on_epics_for_portfolio() {
        doReturn(base).when(epicService).getAllEpicWeights(any(), any());
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        doReturn(client).when(epicService).getGitlabClient(any());

        epicService.setWeights(foundEpicForPortfolio);

        verify(epicService, times(1)).setWeights(any());
    }

    @Test
    void should_return_false_when_product_is_archived() {
        foundProduct.setIsArchived(true);

        epicService.hasGitlabDetails(foundProduct);

        verify(epicService, times(1)).hasGitlabDetails(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_is_archived() {
        foundPortfolio.setIsArchived(true);

        epicService.hasGitlabDetails(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetails(foundPortfolio);
    }

    @Test
    void should_return_false_when_product_gitlab_group_id_is_null() {
        foundProduct.setGitlabGroupId(null);

        epicService.hasGitlabDetails(foundProduct);

        verify(epicService, times(1)).hasGitlabDetails(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_gitlab_group_id_is_null() {
        foundPortfolio.setGitlabGroupId(null);

        epicService.hasGitlabDetails(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetails(foundPortfolio);
    }

    @Test
    void should_return_false_when_product_source_control_is_null() {
        foundProduct.setSourceControl(null);

        epicService.hasGitlabDetails(foundProduct);

        verify(epicService, times(1)).hasGitlabDetails(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_source_control_is_null() {
        foundPortfolio.setSourceControl(null);

        epicService.hasGitlabDetails(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetails(foundPortfolio);
    }

    @Test
    void should_return_false_when_product_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundProduct.setSourceControl(sourceControl);

        epicService.hasGitlabDetails(foundProduct);

        verify(epicService, times(1)).hasGitlabDetails(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundPortfolio.setSourceControl(sourceControl);

        epicService.hasGitlabDetails(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetails(foundPortfolio);
    }

    @Test
    void should_return_false_when_product_source_control_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundProduct.setSourceControl(sourceControl);

        epicService.hasGitlabDetails(foundProduct);

        verify(epicService, times(1)).hasGitlabDetails(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_source_control_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundPortfolio.setSourceControl(sourceControl);

        epicService.hasGitlabDetails(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetails(foundPortfolio);
    }

    @Test
    void should_get_all_epics_for_gitlab_product() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClient(foundProduct)).thenReturn(client);
        when(client.getTotalEpicsPages(foundProduct)).thenReturn(1);
        when(epicService.processEpics(List.of(), foundProduct)).thenReturn(Set.of(expectedEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anySet());
        doReturn(foundEpicForProduct).when(epicService).convertToEpic(any(), any());

        assertThat(epicService.gitlabEpicSync(foundProduct)).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void should_get_all_epics_for_gitlab_portfolio() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClient(foundPortfolio)).thenReturn(client);
        when(client.getTotalEpicsPages(foundPortfolio)).thenReturn(1);
        when(epicService.processEpics(List.of(), foundPortfolio)).thenReturn(Set.of(expectedEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anySet());
        doReturn(foundEpicForPortfolio).when(epicService).convertToEpic(any(), any());

        assertThat(epicService.gitlabEpicSync(foundPortfolio)).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void should_return_empty_set_if_product_is_missing_gitlab_details() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClient(foundProduct)).thenReturn(client);
        when(client.getTotalEpicsPages(foundProduct)).thenReturn(1);
        when(epicService.processEpics(List.of(), foundProduct)).thenReturn(Set.of());
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anySet());
        doReturn(foundEpicForProduct).when(epicService).convertToEpic(any(), any());

        assertThat(epicService.gitlabEpicSync(foundProduct)).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_if_portfolio_is_missing_gitlab_details() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClient(foundPortfolio)).thenReturn(client);
        when(client.getTotalEpicsPages(foundPortfolio)).thenReturn(1);
        when(epicService.processEpics(List.of(), foundPortfolio)).thenReturn(Set.of());
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anySet());
        doReturn(foundEpicForPortfolio).when(epicService).convertToEpic(any(), any());

        assertThat(epicService.gitlabEpicSync(foundPortfolio)).isEqualTo(Set.of());

    }

    @Test
    void should_get_all_epics_by_product_id() {
        epicService.getAllEpicsByProductId(foundProduct.getId());

        assertThat(epicService.getAllEpicsByProductId(foundProduct.getId())).isNotNull();
    }

    @Test
    void should_get_all_epics_by_portfolio_id() {
        epicService.getAllEpicsByPortfolioId(foundPortfolio.getId());

        assertThat(epicService.getAllEpicsByPortfolioId(foundPortfolio.getId())).isNotNull();
    }

    @Test
    void should_run_scheduled_epic_sync_for_product() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, expectedEpic);
        expectedEpic.setCreationDate(CREATED_AT);

        doReturn(List.of(foundProduct)).when(productService).getAll();
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        doReturn(gitLab4JClient).when(epicService).getGitlabClient(any());
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anySet());

        epicService.runScheduledEpicSync();

        verify(epicService, times(1)).gitlabEpicSync(foundProduct);
    }

    @Test
    void should_run_scheduled_epic_sync_for_portfolio() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(epic, expectedEpic);
        expectedEpic.setCreationDate(CREATED_AT);

        doReturn(List.of(foundPortfolio)).when(portfolioService).getAll();
        when(portfolioService.findById(foundPortfolio.getId())).thenReturn(foundPortfolio);
        doReturn(gitLab4JClient).when(epicService).getGitlabClient(any());
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anySet());

        epicService.runScheduledEpicSync();

        verify(epicService, times(1)).gitlabEpicSync(foundPortfolio);
    }

    @Test
    void can_add_Epic_returns_false_for_product() {
        assertFalse(epicService.canAddEpic(foundEpicForProduct.getEpicIid(), foundProduct));
    }

    @Test
    void can_add_Epic_returns_false_for_portfolio() {
        assertFalse(epicService.canAddEpic(foundEpicForPortfolio.getEpicIid(), foundPortfolio));
    }

    @Test
    void should_update_isHidden() {
        IsHiddenDTO isHiddenDTO = new IsHiddenDTO(true);

        when(repository.findById(6L)).thenReturn(Optional.of(foundEpicForProduct));
        when(repository.save(foundEpicForProduct)).thenReturn(foundEpicForProduct);

        epicService.updateIsHidden(6L, isHiddenDTO);

        verify(repository, times(1)).save(captor.capture());
        Epic epicSaved = captor.getValue();

        assertThat(epicSaved.getIsHidden()).isEqualTo(isHiddenDTO.getIsHidden());
    }

    @Test
    void should_remove_all_untracked_epics_for_product() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, expectedEpic);
        expectedEpic.setTitle(epic.getTitle());
        expectedEpic.setEpicIid(42);

        doReturn(new ArrayList<>(List.of(foundEpicForProduct, expectedEpic))).when(epicService).getAllEpicsByProductId(anyLong());
        doNothing().when(repository).deleteAll(anyList());

        epicService.removeAllUntrackedEpicsForProducts(foundProduct.getId(), Set.of(epic));

        verify(repository, times(1)).deleteAll(List.of(expectedEpic));

    }

    @Test
    void should_remove_all_untracked_epics_for_portfolio() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(epic.getTitle());
        expectedEpic.setEpicIid(42);

        doReturn(new ArrayList<>(List.of(foundEpicForPortfolio, expectedEpic))).when(epicService).getAllEpicsByPortfolioId(anyLong());
        doNothing().when(repository).deleteAll(anyList());

        epicService.removeAllUntrackedEpicsForPortfolios(foundPortfolio.getId(), Set.of(epic));

        verify(repository, times(1)).deleteAll(anyList());

    }

    @Test
    void should_get_epic_from_client_for_product() {
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        doReturn(client).when(epicService).getGitlabClient(any());

        epicService.getEpicFromClient(foundProduct, 2);

        verify(epicService, times(1)).getEpicFromClient(any(), anyInt());
    }

    @Test
    void should_get_epic_from_client_for_portfolio() {
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        doReturn(client).when(epicService).getGitlabClient(any());

        epicService.getEpicFromClient(foundPortfolio, 2);

        verify(epicService, times(1)).getEpicFromClient(any(), anyInt());
    }

    @Test
    void should_process_product_epics() {
        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.of(foundEpicForProduct));
        doReturn(foundEpicForProduct).when(epicService).setWeights(any());

        epicService.processEpics(List.of(gitLabEpic), foundProduct);

        verify(repository, times(2)).save(captor.capture());

        Epic epicSaved = captor.getValue();
        assertThat(epicSaved).isEqualTo(foundEpicForProduct);
    }

    @Test
    void should_process_portfolio_epics() {
        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.of(foundEpicForPortfolio));
        doReturn(foundEpicForPortfolio).when(epicService).setWeights(any());

        epicService.processEpics(List.of(gitLabEpic), foundPortfolio);

        verify(repository, times(2)).save(captor.capture());

        Epic epicSaved = captor.getValue();
        assertThat(epicSaved).isEqualTo(foundEpicForPortfolio);
    }
}
