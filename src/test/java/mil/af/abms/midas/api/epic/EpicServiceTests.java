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
import java.util.List;
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

import mil.af.abms.midas.api.deliverable.DeliverableService;
import mil.af.abms.midas.api.dtos.AddGitLabEpicDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;

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
    private DeliverableService deliverableService;
    @MockBean
    private GitLab4JClient client;

    @Captor
    ArgumentCaptor<Epic> captor;

    private static final LocalDateTime CREATED_AT = LocalDateTime.now().minusDays(1L);
    private static final LocalDateTime CLOSED_AT = LocalDateTime.now();

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
    private final Epic foundEpic = Builder.build(Epic.class)
            .with(e -> e.setId(6L))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicUid("3422"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedWeight(0L))
            .with(e -> e.setTotalWeight(0L))
            .with(e -> e.setProduct(foundProduct))
            .get();
    private final GitLabEpic gitLabEpic = Builder.build(GitLabEpic.class)
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setCompletedAt(CLOSED_AT))
            .get();

    @Test
    void can_create_Epic_new() {
        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpic).when(epicService).setWeights(any());
        when(productService.findById(any())).thenReturn(foundProduct);

        epicService.create(new AddGitLabEpicDTO(2, 1L));
        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3422");
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void can_create_Epic_exists() {

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpic).when(epicService).setWeights(any());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findByEpicUid(foundEpic.getEpicUid())).thenReturn(Optional.of(foundEpic));

        epicService.create(new AddGitLabEpicDTO(2, 1L));

        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo("3422");
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void can_update_Epic() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpic, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        doReturn(foundEpic).when(epicService).setWeights(foundEpic);
        doReturn(gitLabEpic).when(client).getEpicFromGroup(anyInt(), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpic));

        epicService.updateById(1L);

        verify(epicService, times(1)).syncEpic(any(), any());

    }

    @Test
    void should_throw_exception_when_trying_to_update_by_id() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpic, epicDuplicate);

        doReturn(gitLabEpic).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findById(anyLong())).thenReturn(Optional.of(foundEpic));

        epicService.updateById(1L);

        verify(epicService, times(1)).syncEpic(any(), any());

    }

    @Test
    void should_get_weights_on_epics() {
        epicService.getAllEpicWeights(client, Optional.of(gitLabEpic));

        verify(epicService, times(1)).getAllEpicWeights(client, Optional.of(gitLabEpic));
    }

    @Test
    void should_return_set_if_product_has_all_gitlab_details() {
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        epicService.hasGitlabDetails(foundProduct);

        verify(epicService, times(1)).hasGitlabDetails(foundProduct);
    }

    @Test
    void should_return_empty_set_if_product_does_not_have_all_gitlab_details() {
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        doReturn(false).when(epicService).hasGitlabDetails(foundProduct);

        assertThat(epicService.getAllGitlabEpicsForProduct(foundProduct.getId())).isEqualTo(Set.of());
    }

    @Test
    void should_get_all_epics_for_gitlab_product() {
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpic, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        doReturn(expectedEpic).when(repository).save(any(Epic.class));
        when(epicService.getGitlabClient(foundProduct)).thenReturn(client);
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        doReturn(Optional.of(foundEpic)).when(repository).findByEpicIid(any());
        when(client.getEpicsFromGroup(foundProduct.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpics(anyLong(), anyList());
        doReturn(foundEpic).when(epicService).convertToEpic(any(), any());

        assertThat(epicService.getAllGitlabEpicsForProduct(foundProduct.getId())).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void should_get_all_epics_by_product_id() {
        epicService.getAllEpicsByProductId(foundProduct.getId());

        assertThat(epicService.getAllEpicsByProductId(foundProduct.getId())).isNotNull();
    }

    @Test
    void should_get_all_epics_for_gitlab_group() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpic, expectedEpic);
        expectedEpic.setTitle(gitLabEpic.getTitle());
        expectedEpic.setId(6L);

        when(repository.save(any(Epic.class))).thenReturn(expectedEpic);
        when(epicService.getGitlabClient(foundProduct)).thenReturn(gitLab4JClient);
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        when(gitLab4JClient.getEpicsFromGroup(foundProduct.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpics(anyLong(), anyList());
        doReturn(expectedEpic).when(epicService).setWeights(any());

        assertThat(epicService.getAllGitlabEpicsForProduct(foundProduct.getId())).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void should_run_scheduled_epic_sync() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, expectedEpic);
        expectedEpic.setCreationDate(CREATED_AT);

        doReturn(List.of(foundProduct.getId())).when(productService).getAllProductIds();
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        doReturn(gitLab4JClient).when(epicService).getGitlabClient(any());
        when(gitLab4JClient.getEpicsFromGroup(foundProduct.getGitlabGroupId())).thenReturn(List.of(gitLabEpic));
        doNothing().when(epicService).removeAllUntrackedEpics(anyLong(), anyList());
        doReturn(expectedEpic).when(epicService).setWeights(any());

        epicService.runScheduledEpicSync();

        verify(repository, times(1)).save(captor.capture());
        Epic epicSaved = captor.getValue();
        epicSaved.setCreationDate(CREATED_AT);

        assertThat(epicSaved).isEqualTo(expectedEpic);
        assertThat(gitLabEpic.getCompletedAt()).isEqualTo(epicSaved.getCompletedAt());
    }

    @Test
    void can_add_Epic_returns_false() {
        assertFalse(epicService.canAddEpic(foundEpic.getEpicIid(), foundProduct));
    }

    @Test
    void should_update_isHidden() {
        IsHiddenDTO isHiddenDTO = new IsHiddenDTO(true);

        when(repository.findById(6L)).thenReturn(Optional.of(foundEpic));
        when(repository.save(foundEpic)).thenReturn(foundEpic);

        epicService.updateIsHidden(6L, isHiddenDTO);

        verify(repository, times(1)).save(captor.capture());
        Epic epicSaved = captor.getValue();

        assertThat(epicSaved.getIsHidden()).isEqualTo(isHiddenDTO.getIsHidden());
    }

}
