package mil.af.abms.midas.api.roadmap;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDate;
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

    private static final LocalDate TEST_DATE = LocalDate.now();
    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private final Roadmap roadmap = Builder.build(Roadmap.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setTitle("roadmap title"))
            .with(t -> t.setStartDate(TEST_DATE))
            .with(t -> t.setDueDate(TEST_DATE))
            .with(t -> t.setCompletedAt(TEST_TIME))
            .with(t -> t.setStatus(RoadmapStatus.COMPLETE))
            .with(t -> t.setDescription("roadmap description"))
            .get();
    private final RoadmapDTO roadmapDTOExpected = Builder.build(RoadmapDTO.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setTitle("roadmap title"))
            .with(t -> t.setStartDate(TEST_DATE))
            .with(t -> t.setDueDate(TEST_DATE))
            .with(t -> t.setCompletedAt(TEST_TIME))
            .with(t -> t.setCreationDate(roadmap.getCreationDate()))
            .with(t -> t.setStatus(RoadmapStatus.COMPLETE))
            .with(t -> t.setDescription("roadmap description"))
            .get();

    @Test
    void should_have_all_roadmapDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Roadmap.class, fields::add);

        assertThat(fields).hasSize(RoadmapDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Roadmap roadmap2 = new Roadmap();
        BeanUtils.copyProperties(roadmap, roadmap2);

        assertThat(roadmap).isEqualTo(roadmap);
        assertThat(roadmap).isNotNull();
        assertThat(roadmap).isNotEqualTo(new User());
        assertThat(roadmap).isNotSameAs(new Roadmap());
        assertThat(roadmap).isEqualTo(roadmap2);
    }

    @Test
    void should_get_properties() {
        assertThat(roadmap.getId()).isEqualTo(1L);
        assertThat(roadmap.getTitle()).isEqualTo("roadmap title");
        assertThat(roadmap.getStartDate()).isEqualTo(TEST_DATE);
        assertThat(roadmap.getDueDate()).isEqualTo(TEST_DATE);
        assertThat(roadmap.getCompletedAt()).isEqualTo(TEST_TIME);
        assertThat(roadmap.getStatus()).isEqualTo(RoadmapStatus.COMPLETE);
        assertThat(roadmap.getDescription()).isEqualTo("roadmap description");
    }

    @Test
    void can_return_dto() {
        assertThat(roadmap.toDto()).isEqualTo(roadmapDTOExpected);
    }
}
