package mil.af.abms.midas.api.epic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
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
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;

@ExtendWith(SpringExtension.class)
@Import(EpicService.class)
class EpicServiceTests {

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
            .with(e -> e.setEpicUid("3422"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedWeight(0L))
            .with(e -> e.setTotalWeight(0L))
            .with(e -> e.setProduct(foundProduct))
            .with((e -> e.setCompletions(Set.of(completion))))
            .get();
    private final Epic foundEpicForPortfolio = Builder.build(Epic.class)
            .with(e -> e.setId(7L))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicUid("3422"))
            .with(e -> e.setEpicIid(21))
            .with(e -> e.setCompletedWeight(0L))
            .with(e -> e.setTotalWeight(0L))
            .with(e -> e.setPortfolio(foundPortfolio))
            .with((e -> e.setCompletions(Set.of(completion))))
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
        doReturn(gitLabEpic).when(epicService).getProductEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpicForProduct).when(epicService).setWeightsForProduct(any());
        when(productService.findById(any())).thenReturn(foundProduct);

        epicService.createForProduct(new AddGitLabEpicWithProductDTO(2, 1L));
        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3422");
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void can_create_Epic_for_portfolio() {
        doReturn(gitLabEpic).when(epicService).getPortfolioEpicFromClient(any(Portfolio.class), anyInt());
        doReturn(foundEpicForPortfolio).when(epicService).setWeightsForPortfolio(any());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);

        epicService.createForPortfolio(new AddGitLabEpicWithPortfolioDTO(2, 1L));
        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3422");
        assertThat(epicSaved.getPortfolio()).isEqualTo(foundPortfolio);
    }

    @Test
    void can_create_Epic_exists_for_product() {

        doReturn(gitLabEpic).when(epicService).getProductEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpicForProduct).when(epicService).setWeightsForProduct(any());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findByEpicUid(foundEpicForProduct.getEpicUid())).thenReturn(Optional.of(foundEpicForProduct));

        epicService.createForProduct(new AddGitLabEpicWithProductDTO(2, 1L));

        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3422");
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void can_create_Epic_exists_for_portfolio() {
        doReturn(gitLabEpic).when(epicService).getPortfolioEpicFromClient(any(Portfolio.class), anyInt());
        doReturn(foundEpicForPortfolio).when(epicService).setWeightsForPortfolio(any());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        when(repository.findByEpicUid(foundEpicForPortfolio.getEpicUid())).thenReturn(Optional.of(foundEpicForPortfolio));

        epicService.createForPortfolio(new AddGitLabEpicWithPortfolioDTO(2, 1L));

        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3422");
        assertThat(epicSaved.getPortfolio()).isEqualTo(foundPortfolio);
    }

    @Test
    void can_update_Epic_for_Product() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getProductEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpicForProduct).when(epicService).setWeightsForProduct(foundEpicForProduct);
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForProduct));

        epicService.updateByIdForProduct(1L);

        verify(epicService, times(1)).syncEpicForProduct(any(), any());
    }

    @Test
    void can_update_Epic_for_Portfolio() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getPortfolioEpicFromClient(any(Portfolio.class), anyInt());
        doReturn(foundEpicForPortfolio).when(epicService).setWeightsForPortfolio(foundEpicForPortfolio);
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForPortfolio));

        epicService.updateByIdForPortfolio(1L);

        verify(epicService, times(1)).syncEpicForPortfolio(any(), any());
    }

    @Test
    void should_throw_exception_when_trying_to_update_by_id_for_product() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getProductEpicFromClient(any(Product.class), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForProduct));
        doNothing().when(completionService).setCompletionTypeToFailure(anyLong());

        epicService.updateByIdForProduct(1L);

        verify(epicService, times(1)).syncEpicForProduct(any(), any());

    }

    @Test
    void should_throw_exception_when_trying_to_update_by_id_for_portfolio() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getPortfolioEpicFromClient(any(Portfolio.class), anyInt());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForPortfolio));
        doNothing().when(completionService).setCompletionTypeToFailure(anyLong());

        epicService.updateByIdForPortfolio(1L);

        verify(epicService, times(1)).syncEpicForPortfolio(any(), any());

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
        doReturn(client).when(epicService).getGitlabClientForProduct(any());

        epicService.setWeightsForProduct(foundEpicForProduct);

        verify(epicService, times(1)).setWeightsForProduct(any());
    }

    @Test
    void should_set_weights_on_epics_for_portfolio() {
        doReturn(base).when(epicService).getAllEpicWeights(any(), any());
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        doReturn(client).when(epicService).getGitlabClientForPortfolio(any());

        epicService.setWeightsForPortfolio(foundEpicForPortfolio);

        verify(epicService, times(1)).setWeightsForPortfolio(any());
    }

    @Test
    void should_return_false_when_product_is_archived() {
        foundProduct.setIsArchived(true);

        epicService.hasGitlabDetailsForProduct(foundProduct);

        verify(epicService, times(1)).hasGitlabDetailsForProduct(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_is_archived() {
        foundPortfolio.setIsArchived(true);

        epicService.hasGitlabDetailsForPortfolio(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetailsForPortfolio(foundPortfolio);
    }

    @Test
    void should_return_false_when_product_gitlab_group_id_is_null() {
        foundProduct.setGitlabGroupId(null);

        epicService.hasGitlabDetailsForProduct(foundProduct);

        verify(epicService, times(1)).hasGitlabDetailsForProduct(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_gitlab_group_id_is_null() {
        foundPortfolio.setGitlabGroupId(null);

        epicService.hasGitlabDetailsForPortfolio(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetailsForPortfolio(foundPortfolio);
    }

    @Test
    void should_return_false_when_product_source_control_is_null() {
        foundProduct.setSourceControl(null);

        epicService.hasGitlabDetailsForProduct(foundProduct);

        verify(epicService, times(1)).hasGitlabDetailsForProduct(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_source_control_is_null() {
        foundPortfolio.setSourceControl(null);

        epicService.hasGitlabDetailsForPortfolio(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetailsForPortfolio(foundPortfolio);
    }

    @Test
    void should_return_false_when_product_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundProduct.setSourceControl(sourceControl);

        epicService.hasGitlabDetailsForProduct(foundProduct);

        verify(epicService, times(1)).hasGitlabDetailsForProduct(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundPortfolio.setSourceControl(sourceControl);

        epicService.hasGitlabDetailsForPortfolio(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetailsForPortfolio(foundPortfolio);
    }

    @Test
    void should_return_false_when_product_source_control_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundProduct.setSourceControl(sourceControl);

        epicService.hasGitlabDetailsForProduct(foundProduct);

        verify(epicService, times(1)).hasGitlabDetailsForProduct(foundProduct);
    }

    @Test
    void should_return_false_when_portfolio_source_control_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundPortfolio.setSourceControl(sourceControl);

        epicService.hasGitlabDetailsForPortfolio(foundPortfolio);

        verify(epicService, times(1)).hasGitlabDetailsForPortfolio(foundPortfolio);
    }

    @Test
    void should_get_all_epics_for_gitlab_product() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClientForProduct(foundProduct)).thenReturn(client);
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        doReturn(Optional.of(foundEpicForProduct)).when(repository).findByEpicIid(any());
        when(client.getEpicsFromGroup(foundProduct.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anyList());
        doReturn(foundEpicForProduct).when(epicService).convertToEpicForProduct(any(), any());

        assertThat(epicService.getAllGitlabEpicsForProduct(foundProduct.getId())).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void should_get_all_epics_for_gitlab_portfolio() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClientForPortfolio(foundPortfolio)).thenReturn(client);
        when(portfolioService.findById(foundPortfolio.getId())).thenReturn(foundPortfolio);
        doReturn(Optional.of(foundEpicForPortfolio)).when(repository).findByEpicIid(any());
        when(client.getEpicsFromGroup(foundPortfolio.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anyList());
        doReturn(foundEpicForPortfolio).when(epicService).convertToEpicForPortfolio(any(), any());

        assertThat(epicService.getAllGitlabEpicsForPortfolio(foundPortfolio.getId())).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void should_return_empty_set_if_product_is_missing_gitlab_details() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClientForProduct(foundProduct)).thenReturn(client);
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        doReturn(Optional.of(foundEpicForProduct)).when(repository).findByEpicIid(any());
        when(client.getEpicsFromGroup(foundProduct.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anyList());
        doReturn(foundEpicForProduct).when(epicService).convertToEpicForProduct(any(), any());
        doReturn(false).when(epicService).hasGitlabDetailsForProduct(any());

        assertThat(epicService.getAllGitlabEpicsForProduct(foundProduct.getId())).isEqualTo(Set.of());
    }

    @Test
    void should_return_empty_set_if_portfolio_is_missing_gitlab_details() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClientForPortfolio(foundPortfolio)).thenReturn(client);
        when(portfolioService.findById(foundPortfolio.getId())).thenReturn(foundPortfolio);
        doReturn(Optional.of(foundEpicForPortfolio)).when(repository).findByEpicIid(any());
        when(client.getEpicsFromGroup(foundPortfolio.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anyList());
        doReturn(foundEpicForPortfolio).when(epicService).convertToEpicForPortfolio(any(), any());
        doReturn(false).when(epicService).hasGitlabDetailsForPortfolio(any());

        assertThat(epicService.getAllGitlabEpicsForPortfolio(foundPortfolio.getId())).isEqualTo(Set.of());
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
    void should_get_all_epics_for_gitlab_group_for_product() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        when(repository.save(any(Epic.class))).thenReturn(expectedEpic);
        when(epicService.getGitlabClientForProduct(foundProduct)).thenReturn(gitLab4JClient);
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        when(gitLab4JClient.getEpicsFromGroup(foundProduct.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anyList());
        doReturn(expectedEpic).when(epicService).setWeightsForProduct(any());

        assertThat(epicService.getAllGitlabEpicsForProduct(foundProduct.getId())).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void should_get_all_epics_for_gitlab_group_for_portfolio() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        when(repository.save(any(Epic.class))).thenReturn(expectedEpic);
        when(epicService.getGitlabClientForPortfolio(foundPortfolio)).thenReturn(gitLab4JClient);
        when(portfolioService.findById(foundPortfolio.getId())).thenReturn(foundPortfolio);
        when(gitLab4JClient.getEpicsFromGroup(foundPortfolio.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anyList());
        doReturn(expectedEpic).when(epicService).setWeightsForPortfolio(any());

        assertThat(epicService.getAllGitlabEpicsForPortfolio(foundPortfolio.getId())).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void should_run_scheduled_epic_sync_for_product() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, expectedEpic);
        expectedEpic.setCreationDate(CREATED_AT);

        doReturn(List.of(foundProduct.getId())).when(productService).getAllProductIds();
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        doReturn(gitLab4JClient).when(epicService).getGitlabClientForProduct(any());
        when(gitLab4JClient.getEpicsFromGroup(foundProduct.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anyList());
        doReturn(expectedEpic).when(epicService).setWeightsForProduct(any());

        epicService.runScheduledEpicSync();

        verify(repository, times(1)).save(captor.capture());
        Epic epicSaved = captor.getValue();
        epicSaved.setCreationDate(CREATED_AT);

        assertThat(epicSaved).isEqualTo(expectedEpic);
        assertThat(gitLabEpic.getCompletedAt()).isEqualTo(epicSaved.getCompletedAt());
    }

    @Test
    void should_run_scheduled_epic_sync_for_portfolio() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, expectedEpic);
        expectedEpic.setCreationDate(CREATED_AT);

        doReturn(List.of(foundPortfolio.getId())).when(portfolioService).getAllPortfolioIds();
        when(portfolioService.findById(foundPortfolio.getId())).thenReturn(foundPortfolio);
        doReturn(gitLab4JClient).when(epicService).getGitlabClientForPortfolio(any());
        when(gitLab4JClient.getEpicsFromGroup(foundPortfolio.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anyList());
        doReturn(expectedEpic).when(epicService).setWeightsForPortfolio(any());

        epicService.runScheduledEpicSync();

        verify(repository, times(1)).save(captor.capture());
        Epic epicSaved = captor.getValue();
        epicSaved.setCreationDate(CREATED_AT);

        assertThat(epicSaved).isEqualTo(expectedEpic);
        assertThat(gitLabEpic.getCompletedAt()).isEqualTo(epicSaved.getCompletedAt());
    }

    @Test
    void can_add_Epic_returns_false_for_product() {
        assertFalse(epicService.canAddEpicWithProduct(foundEpicForProduct.getEpicIid(), foundProduct));
    }

    @Test
    void can_add_Epic_returns_false_for_portfolio() {
        assertFalse(epicService.canAddEpicWithPortfolio(foundEpicForPortfolio.getEpicIid(), foundPortfolio));
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
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setEpicIid(42);

        doReturn(new ArrayList<>(List.of(foundEpicForProduct, expectedEpic))).when(epicService).getAllEpicsByProductId(anyLong());
        doNothing().when(repository).deleteAll(anyList());

        epicService.removeAllUntrackedEpicsForProducts(foundProduct.getId(), List.of(gitLabEpic));

        verify(repository, times(1)).deleteAll(List.of(expectedEpic));

    }

    @Test
    void should_remove_all_untracked_epics_for_portfolio() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setEpicIid(42);

        doReturn(new ArrayList<>(List.of(foundEpicForPortfolio, expectedEpic))).when(epicService).getAllEpicsByPortfolioId(anyLong());
        doNothing().when(repository).deleteAll(anyList());

        epicService.removeAllUntrackedEpicsForPortfolios(foundPortfolio.getId(), List.of(gitLabEpic));

        verify(repository, times(1)).deleteAll(anyList());

    }

    @Test
    void should_get_epic_from_client_for_product() {
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        doReturn(client).when(epicService).getGitlabClientForProduct(any());

        epicService.getProductEpicFromClient(foundProduct, 2);

        verify(epicService, times(1)).getProductEpicFromClient(any(), anyInt());
    }

    @Test
    void should_get_epic_from_client_for_portfolio() {
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        doReturn(client).when(epicService).getGitlabClientForPortfolio(any());

        epicService.getPortfolioEpicFromClient(foundPortfolio, 2);

        verify(epicService, times(1)).getPortfolioEpicFromClient(any(), anyInt());
    }

}
