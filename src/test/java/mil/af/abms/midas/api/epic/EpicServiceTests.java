package mil.af.abms.midas.api.epic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;

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
    @Captor
    ArgumentCaptor<List<Epic>> listCaptor;

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
            .with(e -> e.setProduct(foundProduct))
            .with((e -> e.setCompletions(Set.of(completion))))
            .get();

    private final Epic foundEpicForPortfolio = Builder.build(Epic.class)
            .with(e -> e.setId(7L))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicUid("3-42-2"))
            .with(e -> e.setEpicIid(2))
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

    @Test
    void createOrUpdateForProduct_can_create_Epic_for_product() {
        when(productService.findById(any())).thenReturn(foundProduct);
        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.empty());

        epicService.createOrUpdateForProduct(new AddGitLabEpicWithProductDTO(2, 1L));
        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getId()).isEqualTo(null);
        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void createOrUpdateForProduct_can_update_Epic_for_product() {
        when(productService.findById(any())).thenReturn(foundProduct);
        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.of(foundEpicForProduct));

        epicService.createOrUpdateForProduct(new AddGitLabEpicWithProductDTO(2, 1L));

        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getId()).isEqualTo(6L);
        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void createOrUpdateForProduct_can_create_Epic_for_portfolio() {
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Portfolio.class), anyInt());
        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.empty());

        epicService.createOrUpdateForPortfolio(new AddGitLabEpicWithPortfolioDTO(2, 1L));
        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getId()).isEqualTo(null);
        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getPortfolio()).isEqualTo(foundPortfolio);
    }

    @Test
    void createOrUpdateForProduct_can_update_Epic_for_portfolio() {
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Portfolio.class), anyInt());
        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.of(foundEpicForPortfolio));

        epicService.createOrUpdateForPortfolio(new AddGitLabEpicWithPortfolioDTO(2, 1L));

        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getId()).isEqualTo(7L);
        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3-42-2");
        assertThat(epicSaved.getPortfolio()).isEqualTo(foundPortfolio);
    }

    @Test
    void updateByIdForProduct() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForProduct));

        epicService.updateByIdForProduct(1L);

        verify(epicService, times(1)).syncEpic(any(), any());
        verify(repository, times(1)).save(any());
    }

    @Test
    void updateByIdForPortfolio() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Portfolio.class), anyInt());
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        when(portfolioService.findById(any())).thenReturn(foundPortfolio);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpicForPortfolio));

        epicService.updateByIdForPortfolio(1L);

        verify(epicService, times(1)).syncEpic(any(), any());
        verify(repository, times(1)).save(any());
    }

    @Test
    void hasGitlabDetails_should_return_false_when_product_is_archived() {
        foundProduct.setIsArchived(true);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundProduct);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_portfolio_is_archived() {
        foundPortfolio.setIsArchived(true);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundPortfolio);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_product_gitlab_group_id_is_null() {
        foundProduct.setGitlabGroupId(null);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundProduct);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_portfolio_gitlab_group_id_is_null() {
        foundPortfolio.setGitlabGroupId(null);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundPortfolio);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_product_source_control_is_null() {
        foundProduct.setSourceControl(null);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundProduct);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_portfolio_source_control_is_null() {
        foundPortfolio.setSourceControl(null);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundPortfolio);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_product_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundProduct.setSourceControl(sourceControl);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundProduct);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_portfolio_source_control_token_is_null() {
        sourceControl.setToken(null);
        foundPortfolio.setSourceControl(sourceControl);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundPortfolio);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_product_source_control_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundProduct.setSourceControl(sourceControl);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundProduct);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_false_when_portfolio_source_control_url_is_null() {
        sourceControl.setBaseUrl(null);
        foundPortfolio.setSourceControl(sourceControl);

        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundPortfolio);
        assertFalse(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_true_when_all_product_details_are_populated() {
        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundProduct);
        assertTrue(hasGitlabDetails);
    }

    @Test
    void hasGitlabDetails_should_return_true_when_all_portfolio_details_are_populated() {
        boolean hasGitlabDetails = epicService.hasGitlabDetails(foundPortfolio);
        assertTrue(hasGitlabDetails);
    }

    @Test
    void gitlabEpicSync_should_save_all_epics_for_gitlab_product() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        User user = new User();
        user.setKeycloakUid("localuser");

        when(epicService.getGitlabClient(foundProduct)).thenReturn(client);
        when(client.getTotalEpicsPages(foundProduct)).thenReturn(1);
        when(epicService.processEpics(List.of(), foundProduct)).thenReturn(List.of(expectedEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anyList());
        doReturn(foundEpicForProduct).when(epicService).convertToEpic(any(), any());
        doReturn(user).when(userService).getUserBySecContext();

        epicService.gitlabEpicSync(foundProduct);
        verify(repository, times(1)).saveAll(listCaptor.capture());
        List<Epic> epicsSaved = listCaptor.getValue();

        assertThat(epicsSaved).hasSize(1);
        assertThat(epicsSaved.stream().findFirst().get()).isEqualTo(expectedEpic);
    }

    @Test
    void gitlabEpicSync_should_save_all_epics_for_gitlab_portfolio() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClient(foundPortfolio)).thenReturn(client);
        when(client.getTotalEpicsPages(foundPortfolio)).thenReturn(1);
        when(epicService.processEpics(List.of(), foundPortfolio)).thenReturn(List.of(expectedEpic));
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anyList());
        doReturn(foundEpicForPortfolio).when(epicService).convertToEpic(any(), any());

        epicService.gitlabEpicSync(foundPortfolio);
        verify(repository, times(1)).saveAll(listCaptor.capture());
        List<Epic> epicsSaved = listCaptor.getValue();

        assertThat(epicsSaved).hasSize(1);
        assertThat(epicsSaved.stream().findFirst().get()).isEqualTo(expectedEpic);
    }

    @Test
    void gitlabEpicSync_should_return_empty_set_if_product_is_missing_gitlab_details() {
        Product productNoDetails = new Product();
        BeanUtils.copyProperties(foundProduct, productNoDetails);
        productNoDetails.setIsArchived(true);

        List<Epic> emptyList = epicService.gitlabEpicSync(productNoDetails);
        verify(repository, times(0)).saveAll(anyList());
        assertThat(emptyList).hasSize(0);
    }

    @Test
    void gitlabEpicSync_should_return_empty_set_if_portfolio_is_missing_gitlab_details() {
        Portfolio portfolioNoDetails = new Portfolio();
        BeanUtils.copyProperties(foundPortfolio, portfolioNoDetails);
        portfolioNoDetails.setIsArchived(true);

        List<Epic> emptyList = epicService.gitlabEpicSync(portfolioNoDetails);
        verify(repository, times(0)).saveAll(anyList());
        assertThat(emptyList).hasSize(0);
    }

    @Test
    void getAllEpicsByProductId_should_get_all_epics_by_product_id() {
        epicService.getAllEpicsByProductId(foundProduct.getId());
        verify(repository, times(1)).findAllEpicsByProductId(1L);
    }

    @Test
    void getAllEpicsByProductId_should_return_empty_list_if_not_found() {
        when(repository.findAllEpicsByProductId(100L)).thenReturn(Optional.empty());
        List<Epic> epicList = epicService.getAllEpicsByProductId(100L);
        assertThat(epicList).hasSize(0);
    }

    @Test
    void getAllEpicsByPortfolioId_should_get_all_epics_by_portfolio_id() {
        epicService.getAllEpicsByPortfolioId(foundPortfolio.getId());
        verify(repository, times(1)).findAllEpicsByPortfolioId(1L);
    }

    @Test
    void getAllEpicsByPortfolioId_should_return_empty_list_if_not_found() {
        when(repository.findAllEpicsByPortfolioId(100L)).thenReturn(Optional.empty());
        List<Epic> epicList = epicService.getAllEpicsByPortfolioId(100L);
        assertThat(epicList).hasSize(0);
    }

    @Test
    void runScheduledEpicSync_should_sync_unarchived_portfolios_and_products() {
        Product archivedProduct = new Product();
        archivedProduct.setIsArchived(true);
        Portfolio archivedPortfolio = new Portfolio();
        archivedPortfolio.setIsArchived(true);
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, expectedEpic);
        expectedEpic.setCreationDate(CREATED_AT);

        doReturn(List.of(foundProduct, archivedProduct)).when(productService).getAll();
        doReturn(List.of(foundPortfolio, archivedPortfolio)).when(portfolioService).getAll();
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        when(portfolioService.findById(foundPortfolio.getId())).thenReturn(foundPortfolio);
        doReturn(gitLab4JClient).when(epicService).getGitlabClient(any());
        doNothing().when(epicService).removeAllUntrackedEpicsForProducts(anyLong(), anyList());
        doNothing().when(epicService).removeAllUntrackedEpicsForPortfolios(anyLong(), anyList());

        epicService.runScheduledEpicSync();

        verify(epicService, times(2)).gitlabEpicSync(or(eq(foundProduct), eq(foundPortfolio)));
    }

    @Test
    void canAddEpic_returns_false_for_product_if_epic_doesnt_exist() {
        when(client.findEpicByIdAndGroupId(2, 42)).thenReturn(Optional.empty());
        assertFalse(epicService.canAddEpic(foundEpicForProduct.getEpicIid(), foundProduct));
    }

    @Test
    void canAddEpic_returns_false_for_portfolio_if_epic_doesnt_exist() {
        when(client.findEpicByIdAndGroupId(2, 42)).thenReturn(Optional.empty());
        assertFalse(epicService.canAddEpic(foundEpicForPortfolio.getEpicIid(), foundPortfolio));
    }

    @Test
    void updateIsHidden_should_update_isHidden_to_match_dto() {
        IsHiddenDTO isHiddenDTO = new IsHiddenDTO(true);

        when(repository.findById(6L)).thenReturn(Optional.of(foundEpicForProduct));

        epicService.updateIsHidden(6L, isHiddenDTO);

        verify(repository, times(1)).save(captor.capture());
        Epic epicSaved = captor.getValue();
        assertTrue(epicSaved.getIsHidden());
    }

    @Test
    void removeAllUntrackedEpicsForProducts() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForProduct, expectedEpic);
        expectedEpic.setTitle(epic.getTitle());
        expectedEpic.setEpicIid(42);

        doReturn(new ArrayList<>(List.of(foundEpicForProduct, expectedEpic))).when(epicService).getAllEpicsByProductId(anyLong());

        epicService.removeAllUntrackedEpicsForProducts(foundProduct.getId(), List.of(epic));

        verify(repository, times(1)).deleteAll(List.of(expectedEpic));

    }

    @Test
    void removeAllUntrackedEpicsForPortfolios() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpicForPortfolio, expectedEpic);
        expectedEpic.setTitle(epic.getTitle());
        expectedEpic.setEpicIid(42);

        doReturn(new ArrayList<>(List.of(foundEpicForPortfolio, expectedEpic))).when(epicService).getAllEpicsByPortfolioId(anyLong());

        epicService.removeAllUntrackedEpicsForPortfolios(foundPortfolio.getId(), List.of(epic));

        verify(repository, times(1)).deleteAll(anyList());

    }

    @Test
    void getEpicFromClient_for_product() {
        doReturn(client).when(epicService).getGitlabClient(any());
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());

        GitLabEpic foundEpic = epicService.getEpicFromClient(foundProduct, 2);

        assertThat(foundEpic).isEqualTo(gitLabEpic);
    }

    @Test
    void getEpicFromClient_for_portfolio() {
        doReturn(client).when(epicService).getGitlabClient(any());
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());

        GitLabEpic foundEpic = epicService.getEpicFromClient(foundPortfolio, 2);

        assertThat(foundEpic).isEqualTo(gitLabEpic);
    }

    @Test
    void processEpics_should_sync_product_epics() {
        GitLabEpic unmatchedEpic = new GitLabEpic();
        BeanUtils.copyProperties(gitLabEpic, unmatchedEpic);
        unmatchedEpic.setEpicIid(500);

        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.of(foundEpicForProduct));
        when(repository.findByEpicUid("3-42-500")).thenReturn(Optional.empty());

        List<Epic> epics = epicService.processEpics(List.of(gitLabEpic, unmatchedEpic), foundProduct);

        assertThat(epics).hasSize(2);
        assertTrue(epics.contains(foundEpicForProduct));
    }

    @Test
    void processEpics_should_sync_portfolio_epics() {
        GitLabEpic unmatchedEpic = new GitLabEpic();
        BeanUtils.copyProperties(gitLabEpic, unmatchedEpic);
        unmatchedEpic.setEpicIid(500);

        when(repository.findByEpicUid("3-42-2")).thenReturn(Optional.of(foundEpicForPortfolio));
        when(repository.findByEpicUid("3-42-500")).thenReturn(Optional.empty());

        List<Epic> epics = epicService.processEpics(List.of(gitLabEpic, unmatchedEpic), foundPortfolio);

        assertThat(epics).hasSize(2);
        assertTrue(epics.contains(foundEpicForPortfolio));
    }

    @Test
    void getProductById() {
        doReturn(foundProduct).when(productService).findById(anyLong());

        assertThat(epicService.getProductById(1L)).isEqualTo(foundProduct);
    }

    @Test
    void getPortfolioById() {
        doReturn(foundPortfolio).when(portfolioService).findById(anyLong());

        assertThat(epicService.getPortfolioById(1L)).isEqualTo(foundPortfolio);
    }
}
