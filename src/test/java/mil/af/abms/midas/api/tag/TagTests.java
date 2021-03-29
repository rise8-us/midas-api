package mil.af.abms.midas.api.tag;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.api.user.User;

public class TagTests {

    private static final int ENTITY_DTO_FIELD_OFFSET = 2;

    private final Product product = Builder.build(Product.class)
                        .with(p -> p.setId(3L)).get();
    private final Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setLabel("tag test"))
            .with(t -> t.setDescription("New Tag"))
            .with(t -> t.setColor("#969696")).get();
    private final TagDTO tagDTO = Builder.build(TagDTO.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setLabel("tag test"))
            .with(t -> t.setDescription("New Tag"))
            .with(t -> t.setColor("#969696")).get();

    @Test
    public void should_have_all_tagDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Tag.class, fields::add);

        assertThat(fields.size()).isEqualTo(TagDTO.class.getDeclaredFields().length + ENTITY_DTO_FIELD_OFFSET);
    }

    @Test
    public void should_set_and_get_properties() {
        assertThat(tag.getId()).isEqualTo(1L);
        assertThat(tag.getLabel()).isEqualTo("tag test");
        assertThat(tag.getDescription()).isEqualTo("New Tag");
        assertThat(tag.getColor()).isEqualTo("#969696");
    }

    @Test
    public void should_return_dto() {
        assertThat(tag.toDto()).isEqualTo(tagDTO);
    }

    @Test
    public void should_be_equal() {
        Tag tag2 = Builder.build(Tag.class)
                .with(p -> p.setLabel("tag test")).get();

        assertTrue(tag.equals(tag));
        assertFalse(tag.equals(null));
        assertFalse(tag.equals(new User()));
        assertFalse(tag.equals(new Tag()));
        assertTrue(tag.equals(tag2));
    }

}
