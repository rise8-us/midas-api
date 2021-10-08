package mil.af.abms.midas.api.roadmap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.roadmap.dto.RoadmapDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.RoadmapStatus;

class RoadmapTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private final Roadmap roadmap = Builder.build(Roadmap.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setTitle("roadmap title"))
            .with(t -> t.setTargetDate(TEST_TIME))
            .with(t -> t.setStatus(RoadmapStatus.FUTURE))
            .with(t -> t.setDescription("roadmap description"))
            .get();
    private final RoadmapDTO roadmapDTOExpected = Builder.build(RoadmapDTO.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setTitle("roadmap title"))
            .with(t -> t.setTargetDate(TEST_TIME))
            .with(t -> t.setCreationDate(roadmap.getCreationDate()))
            .with(t -> t.setStatus(RoadmapStatus.FUTURE))
            .with(t -> t.setDescription("roadmap description"))
            .get();

    @Test
    void should_have_all_roadmapDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Roadmap.class, fields::add);

        assertThat(fields.size()).isEqualTo(RoadmapDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Roadmap roadmap2 = new Roadmap();
        BeanUtils.copyProperties(roadmap, roadmap2);

        assertTrue(roadmap.equals(roadmap));
        assertFalse(roadmap.equals(null));
        assertFalse(roadmap.equals(new User()));
        assertFalse(roadmap.equals(new Roadmap()));
        assertTrue(roadmap.equals(roadmap2));
    }

    @Test
    void should_get_properties() {
        assertThat(roadmap.getId()).isEqualTo(1L);
        assertThat(roadmap.getTitle()).isEqualTo("roadmap title");
        assertThat(roadmap.getTargetDate()).isEqualTo(TEST_TIME);
        assertThat(roadmap.getStatus()).isEqualTo(RoadmapStatus.FUTURE);
        assertThat(roadmap.getDescription()).isEqualTo("roadmap description");
    }

    @Test
    void can_return_dto() {
        assertThat(roadmap.toDto()).isEqualTo(roadmapDTOExpected);
    }
}
