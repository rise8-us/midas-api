package mil.af.abms.midas.api.announcement;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.announcement.dto.AnnouncementDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;

class AnnouncementTests {

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
    void should_get_and_set_property() {
        assertThat(announcement.getId()).isEqualTo(1L);
        assertThat(announcement.getCreationDate()).isEqualTo(CREATION_DATE);
        assertThat(announcement.getMessage()).isEqualTo("This is an announcement");
    }

    @Test
    void should_convert_to_dto() {
        assertThat(announcement.toDto()).isEqualTo(announcementDTO);
    }

    @Test
    void should_be_equal() {
        Announcement announcement2 = new Announcement();
        BeanUtils.copyProperties(announcement, announcement2);

        assertThat(announcement).isEqualTo(announcement);
        assertThat(announcement).isNotNull();
        assertThat(announcement).isNotEqualTo(new User());
        assertThat(announcement).isNotSameAs(new Announcement());
        assertThat(announcement).isEqualTo(announcement2);
    }

    @Test
    void should_contain_same_number_of_fields_as_dto() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(announcement.getClass(), fields::add);
        assertThat(fields).hasSize(AnnouncementDTO.class.getDeclaredFields().length);
    }

}

