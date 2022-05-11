package mil.af.abms.midas.api.metrics.appusermetrics;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.helper.JsonSetOfLongConverter;
import mil.af.abms.midas.api.metrics.AbstractMetricsEntity;
import mil.af.abms.midas.api.metrics.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.metrics.dtos.UniqueRoleMetricsDTO;

@Entity @Setter @Getter
@Table(name = "metrics_app_user")
public class AppUserMetrics extends AbstractMetricsEntity<AppUserMetricsDTO> {

    @Column(columnDefinition = "BIGINT")
    private Long uniqueLogins;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonSetOfLongConverter.class)
    private Map<String, Set<Long>> uniqueRoleMetrics = new HashMap<>();

    public AppUserMetricsDTO toDto() {
        return new AppUserMetricsDTO(
                id,
                uniqueLogins,
                getUniqueRoleMetricsDTO(uniqueRoleMetrics)
        );
    }

    private UniqueRoleMetricsDTO getUniqueRoleMetricsDTO(Map<String, Set<Long>> uniqueRoleMetrics) {
        return new UniqueRoleMetricsDTO(
                uniqueRoleMetrics.getOrDefault("ADMIN", Set.of()),
                uniqueRoleMetrics.getOrDefault("PORTFOLIO_LEAD", Set.of()),
                uniqueRoleMetrics.getOrDefault("PRODUCT_MANAGER", Set.of()),
                uniqueRoleMetrics.getOrDefault("TECH_LEAD", Set.of()),
                uniqueRoleMetrics.getOrDefault("DESIGNER", Set.of()),
                uniqueRoleMetrics.getOrDefault("PLATFORM_OPERATOR", Set.of()),
                uniqueRoleMetrics.getOrDefault("PORTFOLIO_ADMIN", Set.of()),
                uniqueRoleMetrics.getOrDefault("STAKEHOLDER", Set.of()),
                uniqueRoleMetrics.getOrDefault("TESTER", Set.of()),
                uniqueRoleMetrics.getOrDefault("UNASSIGNED", Set.of())
        );
    }
}
