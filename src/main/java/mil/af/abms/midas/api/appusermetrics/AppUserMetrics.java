package mil.af.abms.midas.api.appusermetrics;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.appusermetrics.dto.AppUserMetricsDTO;
import mil.af.abms.midas.api.dtos.metrics.UniqueRoleMetricsDTO;
import mil.af.abms.midas.api.helper.JsonConverter;

@Slf4j
@Entity
@Setter
@Getter
@Table(name = "metrics_app_user")
public class AppUserMetrics implements Serializable {

    @Id
    @Column(columnDefinition = "DATE DEFAULT CURRENT_TIMESTAMP")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    protected LocalDate id;

    @Column(columnDefinition = "BIGINT")
    private Long uniqueLogins;

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonConverter.class)
    private Map<String, Set<Object>> uniqueRoleMetrics = new HashMap<>();

    public AppUserMetricsDTO toDto() {
        return new AppUserMetricsDTO(
                id,
                uniqueLogins,
                getUniqueRoleMetricsDTO(uniqueRoleMetrics)
        );
    }

    private UniqueRoleMetricsDTO getUniqueRoleMetricsDTO(Map<String, Set<Object>> uniqueRoleMetrics) {
        return new UniqueRoleMetricsDTO(
                uniqueRoleMetrics.getOrDefault("ADMIN", Set.of()),
                uniqueRoleMetrics.getOrDefault("PORTFOLIO_LEAD", Set.of()),
                uniqueRoleMetrics.getOrDefault("PRODUCT_MANAGER", Set.of()),
                uniqueRoleMetrics.getOrDefault("TECH_LEAD", Set.of()),
                uniqueRoleMetrics.getOrDefault("DESIGNER", Set.of()),
                uniqueRoleMetrics.getOrDefault("PLATFORM_OPERATOR", Set.of()),
                uniqueRoleMetrics.getOrDefault("PORTFOLIO_ADMIN", Set.of()),
                uniqueRoleMetrics.getOrDefault("STAKEHOLDER", Set.of()),
                uniqueRoleMetrics.getOrDefault("UNASSIGNED", Set.of())
        );
    }
}
