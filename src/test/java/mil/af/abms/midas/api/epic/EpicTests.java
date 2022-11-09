package mil.af.abms.midas.api.epic;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.user.User;

public class EpicTests {

    private static final int ENTITY_DTO_FIELD_OFFSET = 1;

    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .get();

    private final Epic epic = Builder.build(Epic.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setCreationDate(LocalDateTime.now()))
            .with(e -> e.setProduct(product))
            .get();

    private final EpicDTO expectedDTO = Builder.build(EpicDTO.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setCreationDate(epic.getCreationDate()))
            .with(e -> e.setProductId(product.getId()))
            .get();

    @Test
    void should_have_all_epicDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Epic.class, field -> {
            if (!field.isAnnotationPresent(Deprecated.class)) fields.add(field);
        });
        assertThat(fields.size()).isEqualTo(EpicDTO.class.getDeclaredFields().length + ENTITY_DTO_FIELD_OFFSET);
    }

    @Test
    void should_be_equal() {
        Epic epic2 = new Epic();
        BeanUtils.copyProperties(epic, epic2);

        assertEquals(epic, epic);
        assertNotEquals(epic, null);
        assertNotEquals(epic, new User());
        assertNotEquals(epic, new Epic());
        assertEquals(epic, epic2);
    }

    @Test
    void should_get_properties() {
        assertThat(epic.getId()).isEqualTo(1L);
    }

    @Test
    void can_return_dto() {
        assertThat(epic.toDto()).isEqualTo(expectedDTO);
    }

}
