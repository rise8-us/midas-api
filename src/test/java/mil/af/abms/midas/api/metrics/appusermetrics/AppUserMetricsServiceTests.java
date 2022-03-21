package mil.af.abms.midas.api.metrics.appusermetrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
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
import java.util.stream.Collectors;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.metrics.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.search.SpecificationsBuilder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(AppUserMetricsService.class)
class AppUserMetricsServiceTests {

    @SpyBean
    AppUserMetricsService service;

    @MockBean
    AppUserMetricsRepository repository;

    @Captor
    ArgumentCaptor<AppUserMetrics> appUserMetricsArgumentCaptor;

    private final LocalDate DATE_ID1 = LocalDate.now();
    private final LocalDate DATE_ID2 = LocalDate.now().plusDays(1L);
    private final AppUserMetrics appUserMetrics1 = Builder.build(AppUserMetrics.class)
            .with(m -> m.setId(DATE_ID1))
            .with(m -> m.setUniqueLogins(2L))
            .get();
    private final AppUserMetrics appUserMetrics2 = Builder.build(AppUserMetrics.class)
            .with(m -> m.setId(DATE_ID2))
            .with(m -> m.setUniqueLogins(5L))
            .get();
    private final List<AppUserMetrics> appUserMetrics = List.of(appUserMetrics1, appUserMetrics2);
    private final Page<AppUserMetrics> page = new PageImpl<AppUserMetrics>(appUserMetrics);
    private final  User user = Builder.build(User.class)
            .with(u -> u.setId(1L))
            .with(u -> u.setRoles(1L))
            .with(u -> u.setKeycloakUid("abc-123"))
            .with(u -> u.setUsername("grogu"))
            .with(u -> u.setLastLogin(LocalDateTime.now()))
            .get();

    @Test
    void should_create_metric() {
        var newAppUserMetricsDTO = new AppUserMetricsDTO();
        newAppUserMetricsDTO.setId(DATE_ID1);
        doNothing().when(service).updateUniqueRoles(any(), any());

        service.create(newAppUserMetricsDTO.getId(), user);

        verify(repository, times(1)).save(appUserMetricsArgumentCaptor.capture());
        AppUserMetrics metricSaved = appUserMetricsArgumentCaptor.getValue();

        assertThat(metricSaved.getId()).isEqualTo(DATE_ID1);
        assertThat(metricSaved.getUniqueLogins()).isEqualTo(0L);
    }

    @Test
    void should_increment_uniqueLogins() {
        doReturn(appUserMetrics1).when(service).findById(any());

        service.incrementUniqueLogins(DATE_ID1);

        verify(service, times(1)).incrementUniqueLogins(DATE_ID1);

        assertThat(appUserMetrics1.getUniqueLogins()).isEqualTo(3L);
    }

    @Test
    void should_update_unique_roles() {
        doReturn(appUserMetrics1).when(service).findById(any());
        doNothing().when(service).updateRoleCountByEnum(any(), any());

        service.updateUniqueRoles(DATE_ID1, user);

        verify(service, times(1)).updateRoleCountByEnum(any(), any());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 0})
    void should_update_role_count_by_enum(Long roleCount) {
        var uniqueRoleCounts = new HashMap<String, Set<Long>>();
        uniqueRoleCounts.put("UNASSIGNED", Set.of(1L));

        user.setRoles(roleCount);

        service.updateRoleCountByEnum(uniqueRoleCounts, user);

        verify(service, times(1)).updateUnassignedRole(any(), any(), any());

        assertThat(user.getRoles()).isEqualTo(roleCount);

    }

    @Test
    void should_get_metric_and_return_metric() throws EntityNotFoundException {
        when(repository.findById(any())).thenReturn(Optional.of(new AppUserMetrics()));

        service.findById(DATE_ID1);

        verify(repository).findById(DATE_ID1);
    }

    @Test
    void should_throw_error_when_id_is_null() throws EntityNotFoundException {
        when(repository.findById(any())).thenReturn(Optional.of(new AppUserMetrics()));

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.findById(null));
        assertThat(e).hasMessage("Failed to find AppUserMetrics");
    }

    @Test
    void should_throw_error_when_id_not_found() throws EntityNotFoundException {
        when(repository.findById(any())).thenReturn(Optional.empty());

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () -> service.findById(DATE_ID1));
        assertThat(e).hasMessage("Failed to find AppUserMetrics");
    }

    @Test
    void should_get_metric_or_null_and_return_metric() throws EntityNotFoundException {
        when(repository.findById(any())).thenReturn(Optional.of(new AppUserMetrics()));

        service.findByIdOrNull(DATE_ID1);

        verify(repository).findById(DATE_ID1);
    }

    @Test
    void should_prepare_paged_response() {
        List<AppUserMetricsDTO> results = service.preparePageResponse(page, new MockHttpServletResponse());

        assertThat(results).isEqualTo(appUserMetrics.stream().map(AppUserMetrics::toDto).collect(Collectors.toList()));
    }

    @Test
    void should_retrieve_all_metrics() {
        SpecificationsBuilder<AppUserMetrics> builder = new SpecificationsBuilder<>();
        Specification<AppUserMetrics> specs = builder.withSearch("id:" + DATE_ID1).build();

        when(repository.findAll(eq(specs), any(PageRequest.class))).thenReturn(page);

        assertThat(service.search(specs, 1, null, null, null).stream().findFirst())
                .isEqualTo(Optional.of(appUserMetrics.get(0)));
    }

    @Test
    void should_retrieve_all_metrics_with_DESC() {
        SpecificationsBuilder<AppUserMetrics> builder = new SpecificationsBuilder<>();
        Specification<AppUserMetrics> specs = builder.withSearch("id:" + DATE_ID1).build();

        when(repository.findAll(eq(specs), any(PageRequest.class))).thenReturn(page);

        assertThat(service.search(specs, 1, null, null, "foo").stream().findFirst())
                .isEqualTo(Optional.of(appUserMetrics.get(0)));
    }

    @Test
    void should_update_uniqueLogins_by_id() {
        when(repository.findById(DATE_ID1)).thenReturn(Optional.of(appUserMetrics1));
        when(repository.save(appUserMetrics1)).thenReturn(appUserMetrics1);
        doNothing().when(service).updateUniqueRoles(any(), any());

        service.updateById(DATE_ID1, user);

        verify(repository, times(1)).save(appUserMetricsArgumentCaptor.capture());
        AppUserMetrics savedAppUserMetrics = appUserMetricsArgumentCaptor.getValue();

        assertThat(savedAppUserMetrics.getUniqueLogins()).isEqualTo(2L);
    }

    @Test
    void should_determine_create() {
        doReturn(null).when(service).findByIdOrNull(any());
        doNothing().when(service).create(any(), any());

        service.determineCreateOrUpdate(DATE_ID1, user);

        verify(service, times(1)).create(DATE_ID1, user);
    }

    @Test
    void should_determine_update() {
        doReturn(appUserMetrics1).when(service).findByIdOrNull(any());
        doNothing().when(service).updateById(any(), any());

        service.determineCreateOrUpdate(DATE_ID1, user);

        verify(service, times(1)).updateById(DATE_ID1, user);
    }

}
