package mil.af.abms.midas.api.deliverable;

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

import mil.af.abms.midas.api.deliverable.dto.DeliverableDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.release.Release;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProgressionStatus;

class DeliverableTests {

    private final User assignedTo = Builder.build(User.class).with(u -> u.setId(2L)).get();
    private final Set<Release> releases = Set.of(Builder.build(Release.class).with(p -> p.setId(3L)).get());
    private final Product product = Builder.build(Product.class).with(p -> p.setId(4L)).get();
    private final Deliverable deliverable = Builder.build(Deliverable.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setTitle("title"))
            .with(d -> d.setProduct(product))
            .with(d -> d.setChildren(Set.of()))
            .with(d -> d.setStatus(ProgressionStatus.NOT_STARTED))
            .with(d -> d.setPosition(0))
            .with(d -> d.setReleases(releases))
            .with(d -> d.setPerformanceMeasure(null))
            .with(d -> d.setAssignedTo(assignedTo))
            .with(d -> d.setTargets(Set.of()))
            .get();
    private final DeliverableDTO deliverableDTOExpected = Builder.build(DeliverableDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setTitle("title"))
            .with(d -> d.setProductId(product.getId()))
            .with(d -> d.setChildren(List.of()))
            .with(d -> d.setStatus(ProgressionStatus.NOT_STARTED))
            .with(d -> d.setIndex(0))
            .with(d -> d.setReleaseIds(Set.of(3L)))
            .with(d -> d.setCreationDate(deliverable.getCreationDate()))
            .with(d -> d.setPerformanceMeasureId(null))
            .with(d -> d.setAssignedToId(assignedTo.getId()))
            .with(d -> d.setIsArchived(false))
            .with(d -> d.setTargetIds(Set.of()))
            .get();

    @Test
    void should_have_all_deliverableDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Deliverable.class, fields::add);
        assertThat(fields.size()).isEqualTo(DeliverableDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Deliverable deliverable2 = new Deliverable();
        BeanUtils.copyProperties(deliverable, deliverable2);

        assertTrue(deliverable.equals(deliverable));
        assertFalse(deliverable.equals(null));
        assertFalse(deliverable.equals(new User()));
        assertFalse(deliverable.equals(new Deliverable()));
        assertTrue(deliverable.equals(deliverable2));
    }

    @Test
    void should_get_properties() {
        assertThat(deliverable.getId()).isEqualTo(1L);
        assertThat(deliverable.getTitle()).isEqualTo("title");
    }

    @Test
    void can_return_dto() {
        assertThat(deliverable.toDto()).isEqualTo(deliverableDTOExpected);
    }
}
