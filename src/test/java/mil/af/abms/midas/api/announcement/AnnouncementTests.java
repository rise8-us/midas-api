package mil.af.abms.midas.api.announcement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;
import mil.af.abms.midas.api.helper.Builder;


public class AnnouncementTests {

    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    Announcement announcement = Builder.build(Announcement.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setMessage("This is an announcement")).get();
    AnnouncementDTO announcementDTO = Builder.build(AnnouncementDTO.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setMessage("This is an announcement")).get();

    @Test
    public void should_get_and_set_property() {
        assertThat(announcement.getId()).isEqualTo(1L);
        assertThat(announcement.getCreationDate()).isEqualTo(CREATION_DATE);
        assertThat(announcement.getMessage()).isEqualTo("This is an announcement");
    }

    @Test
    public void should_convert_to_dto() {
        assertThat(announcement.toDto()).isEqualTo(announcementDTO);
    }

    @Test
    public void should_be_equal() {
        Announcement announcement2 = Builder.build(Announcement.class)
                .with(u -> u.setMessage("This is an announcement")).get();

        assertTrue(announcement.equals(announcement));
        assertFalse(announcement.equals(null));
        assertFalse(announcement.equals(new Announcement()));
        assertTrue(announcement.equals(announcement2));
    }

    @Test
    public void should_contain_same_number_of_fields_as_dto() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(announcement.getClass(), fields::add);
        assertThat(fields.size()).isEqualTo(AnnouncementDTO.class.getDeclaredFields().length);
    }

}

