package mil.af.abms.midas.api.missionthread;

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
import mil.af.abms.midas.api.missionthread.dto.MissionThreadDTO;
import mil.af.abms.midas.api.user.User;

class MissionThreadTests {

    private final MissionThread missionThread = Builder.build(MissionThread.class)
            .with(m -> m.setId(1L))
            .with(m -> m.setTitle("title"))
            .get();
    private final MissionThreadDTO missionThreadDTO = Builder.build(MissionThreadDTO.class)
            .with(m -> m.setCreationDate(missionThread.getCreationDate()))
            .with(m -> m.setId(missionThread.getId()))
            .with(m -> m.setTitle(missionThread.getTitle()))
            .with(m -> m.setCapabilityIds(Set.of()))
            .with(m -> m.setIsArchived(false))
            .get();

    @Test
    void should_have_all_missionThreadDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(MissionThread.class, fields::add);

        assertThat(fields.size()).isEqualTo(MissionThreadDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        MissionThread missionThread2 = new MissionThread();
        BeanUtils.copyProperties(missionThread, missionThread2);

        assertTrue(missionThread.equals(missionThread));
        assertFalse(missionThread.equals(null));
        assertFalse(missionThread.equals(new User()));
        assertFalse(missionThread.equals(new MissionThread()));
        assertTrue(missionThread.equals(missionThread2));
    }

    @Test
    void should_get_properties() {
        assertThat(missionThread.getId()).isEqualTo(1L);
        assertThat(missionThread.getTitle()).isEqualTo("title");
    }

    @Test
    void can_return_dto() {
        assertThat(missionThread.toDto()).isEqualTo(missionThreadDTO);
    }
}
