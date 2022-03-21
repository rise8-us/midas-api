package mil.af.abms.midas.api.metrics.metricspageview;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.metrics.dtos.MetricsPageViewDTO;

class MetricsPageViewTests {

    private final LocalDate DATE_ID = LocalDate.now();
    private final Map<String, Set<Long>> pageViewsTest = new HashMap<>(
            Map.of("foo/bar", Set.of(1L))
    );

    private final MetricsPageView metricsPageView = Builder.build(MetricsPageView.class)
            .with(a -> a.setId(DATE_ID))
            .with(a -> a.setPageViews(pageViewsTest))
            .get();
    private final MetricsPageViewDTO metricsPageViewDTO = Builder.build(MetricsPageViewDTO.class)
            .with(a -> a.setId(DATE_ID))
            .with(a -> a.setPageViews(pageViewsTest))
            .get();

    @Test
    void should_have_all_metrics_page_view_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(MetricsPageView.class, fields::add);

        assertThat(fields.size()).isEqualTo(MetricsPageViewDTO.class.getDeclaredFields().length);
    }

    @Test
    void should__get_properties() {
        assertThat(metricsPageView.getId()).isEqualTo(DATE_ID);
        assertThat(metricsPageView.getPageViews()).isEqualTo(pageViewsTest);
    }

    @Test
    void should_return_dto() {
        assertThat(metricsPageView.toDto()).isEqualTo(metricsPageViewDTO);
    }

}
