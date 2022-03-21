package mil.af.abms.midas.api.metrics.metricspageview;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.metrics.metricspageview.dto.CreateOrUpdatePageViewsDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;

@ExtendWith(SpringExtension.class)
@Import(MetricsPageViewService.class)
class MetricsPageViewServiceTests {

    @SpyBean
    MetricsPageViewService service;

    @MockBean
    UserService userService;

    @MockBean
    MetricsPageViewRepository repository;

    @Captor
    ArgumentCaptor<MetricsPageView> metricsPageViewArgumentCaptor;

    private final LocalDate DATE_ID = LocalDate.now();
    private final String TEST_PATHNAME = "products/18/ogsms";

    private final MetricsPageView metricsPageView = Builder.build(MetricsPageView.class)
            .with(m -> m.setId(DATE_ID))
            .get();
    private final CreateOrUpdatePageViewsDTO createOrUpdatePageViewDTO = Builder.build(CreateOrUpdatePageViewsDTO.class)
            .with(m -> m.setPathname(TEST_PATHNAME))
            .get();
    private final User user = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setRoles(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setLastLogin(LocalDateTime.now()))
            .get();
    private final Map<String, Set<Long>> pageViewsMock_1 = new HashMap<>(
            Map.of(TEST_PATHNAME, Set.of(1L))
    );
    private final Map<String, Set<Long>> pageViewsMock_2 = new HashMap<>(
            Map.of(TEST_PATHNAME, Set.of(2L))
    );

    @Test
    void should_create() {
        when(userService.getUserBySecContext()).thenReturn(user);
        when(repository.save(any(MetricsPageView.class))).thenReturn(metricsPageView);

        service.create(DATE_ID, createOrUpdatePageViewDTO);

        verify(repository, times(1)).save(metricsPageViewArgumentCaptor.capture());
        MetricsPageView metricSaved = metricsPageViewArgumentCaptor.getValue();

        assertThat(metricSaved.getId()).isEqualTo(DATE_ID);
        assertThat(metricSaved.getPageViews()).isEqualTo(pageViewsMock_1);
    }

    @Test
    void should_updateById() {
        MetricsPageView existingMetricsPageView = new MetricsPageView();
        BeanUtils.copyProperties(metricsPageView, existingMetricsPageView);
        existingMetricsPageView.setPageViews(pageViewsMock_2);
        var expected = new HashMap<>(Map.of(TEST_PATHNAME, Set.of(1L, 2L)));

        doReturn(existingMetricsPageView).when(service).findById(DATE_ID);
        when(userService.getUserBySecContext()).thenReturn(user);

        service.updateById(DATE_ID, createOrUpdatePageViewDTO);
        verify(repository, times(1)).save(metricsPageViewArgumentCaptor.capture());
        MetricsPageView metricSaved = metricsPageViewArgumentCaptor.getValue();

        assertThat(metricSaved.getId()).isEqualTo(DATE_ID);
        assertThat(metricSaved.getPageViews()).isEqualTo(expected);
    }

    @Test
    void should_determine_create() {
        doReturn(null).when(service).findByIdOrNull(any());
        doReturn(metricsPageView).when(service).create(any(), any());

        service.determineCreateOrUpdate(createOrUpdatePageViewDTO);

        verify(service, times(1)).create(DATE_ID, createOrUpdatePageViewDTO);
    }

    @Test
    void should_determine_update() {
        doReturn(metricsPageView).when(service).findByIdOrNull(any());
        doReturn(metricsPageView).when(service).updateById(any(), any());

        service.determineCreateOrUpdate(createOrUpdatePageViewDTO);

        verify(service, times(1)).updateById(DATE_ID, createOrUpdatePageViewDTO);
    }

}
