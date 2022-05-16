package mil.af.abms.midas.api.metrics.appusermetrics;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.metrics.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.metrics.dtos.UniqueRoleMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;

class AppUserMetricsTests {

    private final LocalDate DATE_ID = LocalDate.now();

    private final AppUserMetrics appUserMetrics = Builder.build(AppUserMetrics.class)
            .with(a -> a.setId(DATE_ID))
            .with(a -> a.setUniqueLogins(2L))
            .get();
    private final UniqueRoleMetricsDTO uniqueRoleMetricsDTO = Builder.build(UniqueRoleMetricsDTO.class)
            .with(u -> u.setAdmins(Set.of()))
            .with(u -> u.setDesigners(Set.of()))
            .with(u -> u.setPlatformOperators(Set.of()))
            .with(u -> u.setPortfolioAdmins(Set.of()))
            .with(u -> u.setPortfolioLeads(Set.of()))
            .with(u -> u.setProductManagers(Set.of()))
            .with(u -> u.setStakeholders(Set.of()))
            .with(u -> u.setTechLeads(Set.of()))
            .with(u -> u.setTesters(Set.of()))
            .with(u -> u.setUnassigned(Set.of()))
            .get();
    private final AppUserMetricsDTO appUserMetricsDTO = Builder.build(AppUserMetricsDTO.class)
            .with(a -> a.setId(DATE_ID))
            .with(a -> a.setUniqueLogins(2L))
            .with(a -> a.setUniqueRoleMetrics(uniqueRoleMetricsDTO))
            .get();

    @Test
    void should_have_all_app_user_metrics_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(AppUserMetrics.class, fields::add);

        assertThat(fields.size()).isEqualTo(AppUserMetricsDTO.class.getDeclaredFields().length);
    }

    @Test
    void should__get_properties() {
        assertThat(appUserMetrics.getId()).isEqualTo(DATE_ID);
        assertThat(appUserMetrics.getUniqueLogins()).isEqualTo(2L);
    }

    @Test
    void should_return_dto() {
        assertThat(appUserMetrics.toDto()).isEqualTo(appUserMetricsDTO);
    }

}
