package mil.af.abms.midas.api.capability;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.capability.dto.CapabilityDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;

public class CapabilityTests {

    private final Capability capability = Builder.build(Capability.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setTitle("title"))
            .with(c -> c.setDescription("description"))
            .with(c -> c.setReferenceId(0))
            .with((c -> c.setDeliverables(Set.of())))
            .get();
    private final CapabilityDTO capabilityDTO = Builder.build(CapabilityDTO.class)
            .with(c -> c.setCreationDate(capability.getCreationDate()))
            .with(c -> c.setId(1L))
            .with(c -> c.setTitle("title"))
            .with(c -> c.setDescription("description"))
            .with(c -> c.setReferenceId(0))
            .with(c -> c.setPerformanceMeasureIds(Set.of()))
            .with(c -> c.setDeliverableIds(Set.of()))
            .with(c -> c.setIsArchived(false))
            .get();

    @Test
    void should_have_all_DTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Capability.class, fields::add);

        assertThat(fields.size()).isEqualTo(CapabilityDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Capability capability2 = new Capability();
        BeanUtils.copyProperties(capability, capability2);

        assertEquals(capability, capability);
        assertNotEquals(null, capability);
        assertNotEquals(capability, new User());
        assertNotEquals(capability, new Capability());
        assertEquals(capability, capability2);
        assertFalse(capability.equals(null));
    }

    @Test
    void should_get_properties() {
        assertThat(capability.getId()).isEqualTo(1L);
        assertThat(capability.getTitle()).isEqualTo("title");
        assertThat(capability.getDescription()).isEqualTo("description");
        assertThat(capability.getReferenceId()).isEqualTo(0);
    }

    @Test
    void can_return_dto() {
        assertThat(capability.toDto()).isEqualTo(capabilityDTO);
    }
}
