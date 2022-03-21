package mil.af.abms.midas.api.metrics.metricspageview;

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
import mil.af.abms.midas.api.metrics.dtos.MetricsPageViewDTO;

@Entity @Setter @Getter
@Table(name = "metrics_page_view")
public class MetricsPageView extends AbstractMetricsEntity<MetricsPageViewDTO> {

    @SuppressWarnings("JpaAttributeTypeInspection")
    @Column(columnDefinition = "JSON")
    @Convert(converter = JsonSetOfLongConverter.class)
    private Map<String, Set<Long>> pageViews = new HashMap<>();

    public MetricsPageViewDTO toDto() {
        return new MetricsPageViewDTO(
                id,
                pageViews
        );
    }
}
