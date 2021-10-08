package mil.af.abms.midas.api.epic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;

import mil.af.abms.midas.api.dtos.AddGitLabEpicDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.EpicConversion;

@ExtendWith(SpringExtension.class)
@Import(EpicService.class)
class EpicServiceTests {

    @SpyBean
    private EpicService epicService;
    @MockBean
    private EpicRepository repository;
    @MockBean
    private ProductService productService;

    @Captor
    ArgumentCaptor<Epic> captor;

    final private SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setId(3L))
            .with(sc -> sc.setToken("fake_token"))
            .with(sc -> sc.setBaseUrl("fake_url"))
            .get();

    final private Product foundProduct = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setGitlabGroupId(42))
            .with(p -> p.setSourceControl(sourceControl))
            .get();

    final private Epic foundEpic = Builder.build(Epic.class)
            .with(e -> e.setId(6L))
            .with(e -> e.setTitle("whoa this is epic"))
            .with(e -> e.setEpicUid(3422L))
            .with(e -> e.setEpicIid(2))
            .with(e -> e.setProduct(foundProduct))
            .get();

    final private EpicConversion epicConversion = Builder.build(EpicConversion.class)
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicIid(2))
            .get();

    @Test
    void can_create_Epic_new() {
        doReturn(epicConversion).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);

        epicService.create(new AddGitLabEpicDTO(2, 1L));
        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo(3422L);
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void can_create_Epic_exists() {

        doReturn(epicConversion).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findByEpicUid(foundEpic.getEpicUid())).thenReturn(Optional.of(foundEpic));

        epicService.create(new AddGitLabEpicDTO(2, 1L));

        verify(repository, times(2)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isEqualTo("title");
        assertThat(epicSaved.getEpicUid()).isEqualTo(3422L);
        assertThat(epicSaved.getProduct()).isEqualTo(foundProduct);
    }

    @Test
    void can_update_Epic() {
        var epicDuplicate = new Epic();
        BeanUtils.copyProperties(foundEpic, epicDuplicate);

        doReturn(epicConversion).when(epicService).getEpicFromClient(any(Product.class), anyInt());
        when(productService.findById(any())).thenReturn(foundProduct);
        when(repository.findById(any())).thenReturn(Optional.of(epicDuplicate));

        epicService.updateById(1L);

        verify(repository, times(1)).save(captor.capture());
        var epicSaved = captor.getValue();

        assertThat(epicSaved.getTitle()).isNotEqualTo(foundEpic.getTitle());
        assertThat(epicSaved.getTitle()).isEqualTo("title");
    }

    @Test
    void should_get_all_epics_for_gitlab_group() {
        var gitLab4JClient = Mockito.mock(GitLab4JClient.class);
        var expectedEpic = new Epic();
        BeanUtils.copyProperties(foundEpic, expectedEpic);
        expectedEpic.setTitle(epicConversion.getTitle());
        expectedEpic.setId(6L);

        when(repository.save(any(Epic.class))).thenReturn(expectedEpic);
        when(epicService.getGitlabClient(foundProduct)).thenReturn(gitLab4JClient);
        when(productService.findById(foundProduct.getId())).thenReturn(foundProduct);
        when(gitLab4JClient.getEpicsFromGroup(sourceControl, foundProduct.getGitlabGroupId())).thenReturn(List.of(epicConversion));

        assertThat(epicService.getAllGitlabEpicsForProduct(foundProduct.getId())).isEqualTo(Set.of(expectedEpic));
    }

    @Test
    void can_add_Epic_returns_false() {
        assertFalse(epicService.canAddEpic(foundEpic.getEpicIid(), foundProduct));
    }

}
