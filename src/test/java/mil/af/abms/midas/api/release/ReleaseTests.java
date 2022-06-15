package mil.af.abms.midas.api.release;

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
import mil.af.abms.midas.api.release.dto.ReleaseDTO;
import mil.af.abms.midas.api.user.User;

public class ReleaseTests {

    private static final int ENTITY_DTO_FIELD_OFFSET = 3;
    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private final Release release = Builder.build(Release.class)
            .with(r -> r.setId(1L))
            .with(r -> r.setCreationDate(TEST_TIME))
            .with(r -> r.setName("bar"))
            .with(r -> r.setDescription("foo"))
            .with(r -> r.setReleasedAt(TEST_TIME))
            .with(r -> r.setTagName("fizz"))
            .get();

    private final ReleaseDTO releaseDTO = Builder.build(ReleaseDTO.class)
            .with(r -> r.setId(1L))
            .with(r -> r.setName("bar"))
            .with(r -> r.setDescription("foo"))
            .with(r -> r.setReleasedAt(TEST_TIME))
            .with(r -> r.setTagName("fizz"))
            .get();
    
    @Test
    public void should_have_all_releaseDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Release.class, fields::add);

        assertThat(fields.size()).isEqualTo(ReleaseDTO.class.getDeclaredFields().length + ENTITY_DTO_FIELD_OFFSET);
    }

    @Test
    void should_be_equal() {
        Release release2 = new Release();
        BeanUtils.copyProperties(release, release2);

        assertTrue(release.equals(release));
        assertFalse(release.equals(null));
        assertFalse(release.equals(new User()));
        assertFalse(release.equals(new Release()));
        assertTrue(release.equals(release2));
    }

    @Test
    void should_get_properties() {
        assertThat(release.getId()).isEqualTo(1L);
        assertThat(release.getTagName()).isEqualTo("fizz");
        assertThat(release.getCreationDate()).isEqualTo(TEST_TIME);
        assertThat(release.getReleasedAt()).isEqualTo(TEST_TIME);
        assertThat(release.getName()).isEqualTo("bar");
        assertThat(release.getDescription()).isEqualTo("foo");
    }

    @Test
    void can_return_dto() {
        assertThat(release.toDto()).isEqualTo(releaseDTO);
    }
}
