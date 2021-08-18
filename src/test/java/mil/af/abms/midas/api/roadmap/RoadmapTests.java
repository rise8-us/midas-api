package mil.af.abms.midas.api.roadmap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.persona.Persona;
import mil.af.abms.midas.api.persona.dto.PersonaDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.user.User;

class RoadmapTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private final Set<User> users = Set.of(Builder.build(User.class).with(u -> u.setId(3L)).get());
    private final Set<Project> projects = Set.of(Builder.build(Project.class).with(u -> u.setId(3L)).get());
    private final Set<Product> products = Set.of(Builder.build(Product.class).with(u -> u.setId(4L)).get());
    private final Persona persona = Builder.build(Persona.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setTitle("MIDAS"))
            .with(t -> t.setIsSupported(false))
            .with(t -> t.setCreationDate(TEST_TIME))
            .with(t -> t.setDescription("dev persona"))
            .get();
    private final PersonaDTO personaDTOExpected = Builder.build(PersonaDTO.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setTitle("MIDAS"))
            .with(t -> t.setIsSupported(false))
            .with(t -> t.setDescription("dev persona"))
            .with(t -> t.setCreationDate(TEST_TIME))
            .get();

    @Test
    void should_have_all_personaDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Persona.class, fields::add);

        assertThat(fields.size()).isEqualTo(PersonaDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Persona persona2 = new Persona();
        BeanUtils.copyProperties(persona, persona2);

        assertTrue(persona.equals(persona));
        assertFalse(persona.equals(null));
        assertFalse(persona.equals(new User()));
        assertFalse(persona.equals(new Persona()));
        assertTrue(persona.equals(persona2));
    }

    @Test
    void should_get_properties() {
        assertThat(persona.getId()).isEqualTo(1L);
        assertThat(persona.getTitle()).isEqualTo("MIDAS");
        assertFalse(persona.getIsSupported());
        assertThat(persona.getCreationDate()).isEqualTo(TEST_TIME);
        assertThat(persona.getDescription()).isEqualTo("dev persona");
    }

    @Test
    void can_return_dto() {
        assertThat(persona.toDto()).isEqualTo(personaDTOExpected);
    }
}
