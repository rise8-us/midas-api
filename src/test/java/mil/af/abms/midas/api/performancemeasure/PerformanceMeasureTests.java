package mil.af.abms.midas.api.performancemeasure;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.performancemeasure.dto.PerformanceMeasureDTO;
import mil.af.abms.midas.api.user.User;

class PerformanceMeasureTests {

    private final PerformanceMeasure performanceMeasure = Builder.build(PerformanceMeasure.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setTitle("title"))
            .with(p -> p.setReferenceId(0))
            .with(p -> p.setCapability(null))
            .get();
    private final PerformanceMeasureDTO performanceMeasureDTO = Builder.build(PerformanceMeasureDTO.class)
            .with(p -> p.setCreationDate(performanceMeasure.getCreationDate()))
            .with(p -> p.setId(1L))
            .with(p -> p.setTitle("title"))
            .with(p -> p.setReferenceId(0))
            .with(p -> p.setDeliverableIds(Set.of()))
            .with(p -> p.setCapabilityId(null))
            .with(p -> p.setIsArchived(false))
            .get();

    @Test
    void should_have_all_performanceMeasureDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(PerformanceMeasure.class, fields::add);

        assertThat(fields.size()).isEqualTo(PerformanceMeasureDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        PerformanceMeasure performanceMeasure2 = new PerformanceMeasure();
        BeanUtils.copyProperties(performanceMeasure, performanceMeasure2);

        assertTrue(performanceMeasure.equals(performanceMeasure));
        assertFalse(performanceMeasure.equals(null));
        assertFalse(performanceMeasure.equals(new User()));
        assertFalse(performanceMeasure.equals(new PerformanceMeasure()));
        assertTrue(performanceMeasure.equals(performanceMeasure2));
    }

    @Test
    void should_get_properties() {
        assertThat(performanceMeasure.getId()).isEqualTo(1L);
        assertThat(performanceMeasure.getTitle()).isEqualTo("title");
        assertThat(performanceMeasure.getReferenceId()).isEqualTo(0);
        assertThat(performanceMeasure.getCapability()).isEqualTo(null);
    }

    @Test
    void can_return_dto() {
        assertThat(performanceMeasure.toDto()).isEqualTo(performanceMeasureDTO);
    }
}
