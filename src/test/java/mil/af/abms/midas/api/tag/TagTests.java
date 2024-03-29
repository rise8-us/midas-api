package mil.af.abms.midas.api.tag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.TagType;

public class TagTests {

    private static final int ENTITY_DTO_FIELD_OFFSET = 3;

    private final User user = Builder.build(User.class)
            .with(p -> p.setId(1L)).get();
    private final Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setLabel("tag test"))
            .with(t -> t.setDescription("New Tag"))
            .with(t -> t.setCreatedBy(user))
            .with(t -> t.setColor("#969696")).get();
    private final TagDTO tagDTO = Builder.build(TagDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setLabel("tag test"))
            .with(d -> d.setDescription("New Tag"))
            .with(d -> d.setCreatedById(user.getId()))
            .with(d -> d.setColor("#969696"))
            .with(d -> d.setTagType(TagType.ALL))
            .get();

    @Test
    public void should_have_all_tagDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Tag.class, fields::add);

        assertThat(fields).hasSize(TagDTO.class.getDeclaredFields().length + ENTITY_DTO_FIELD_OFFSET);
    }

    @Test
    public void should_set_and_get_properties() {
        assertThat(tag.getId()).isEqualTo(1L);
        assertThat(tag.getLabel()).isEqualTo("tag test");
        assertThat(tag.getDescription()).isEqualTo("New Tag");
        assertThat(tag.getColor()).isEqualTo("#969696");
        assertThat(tag.getCreatedBy()).isEqualTo(user);
    }

    @Test
    public void should_return_dto() {
        assertThat(tag.toDto()).isEqualTo(tagDTO);
    }

    @Test
    public void should_be_equal() {
        Tag tag2 = new Tag();
        BeanUtils.copyProperties(tag, tag2);

        assertEquals(tag, tag);
        assertNotEquals(tag, null);
        assertNotEquals(tag, new User());
        assertNotEquals(tag, new Tag());
        assertEquals(tag, tag2);
    }

    @Test
    public void should_return_dto_with_null_fields() {
        Tag tagNullCreateBy = Builder.build(Tag.class)
                .with(t -> t.setId(3L))
                .with(t -> t.setLabel("Null name"))
                .with(t -> t.setColor("#123456"))
                .get();
        TagDTO tagNullCreatedByDTO = Builder.build(TagDTO.class)
                .with(t -> t.setId(3L))
                .with(t -> t.setLabel("Null name"))
                .with(t -> t.setColor("#123456"))
                .with(d -> d.setTagType(TagType.ALL))
                .get();

        assertThat(tagNullCreateBy.toDto()).isEqualTo(tagNullCreatedByDTO);
    }
}
