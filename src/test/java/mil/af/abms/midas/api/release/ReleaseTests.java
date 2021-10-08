package mil.af.abms.midas.api.release;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

public class ReleaseTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();
    
    private final Release release = Builder.build(Release.class)
            .with(r -> r.setId(1L))
            .with(r -> r.setTitle("title"))
            .with(r -> r.setTargetDate(TEST_TIME))
            .with(r -> r.setStatus(ProgressionStatus.NOT_STARTED))
            .get();
    private final ReleaseDTO expectedDTO = Builder.build(ReleaseDTO.class)
            .with(r -> r.setId(1L))
            .with(r -> r.setTitle("title"))
            .with(r -> r.setCreationDate(release.getCreationDate()))
            .with(r -> r.setTargetDate(TEST_TIME))
            .with(r -> r.setStatus(ProgressionStatus.NOT_STARTED))
            .with(r -> r.setDeliverableIds(Set.of()))
            .with(r -> r.setIsArchived(false))
            .get();
    
    @Test
    public void should_have_all_releaseDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Release.class, fields::add);

        assertThat(fields.size()).isEqualTo(ReleaseDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_get_properties() {
        assertThat(release.getId()).isEqualTo(1L);
        assertThat(release.getTitle()).isEqualTo("title");
        assertThat(release.getTargetDate()).isEqualTo(TEST_TIME);
        assertThat(release.getStatus()).isEqualTo(ProgressionStatus.NOT_STARTED);
    }

    @Test
    void can_return_dto() {
        assertThat(release.toDto()).isEqualTo(expectedDTO);
    }
}
