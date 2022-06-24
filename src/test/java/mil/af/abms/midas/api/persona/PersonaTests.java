package mil.af.abms.midas.api.persona;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.persona.dto.PersonaDTO;
import mil.af.abms.midas.api.user.User;

class PersonaTests {

    private final Persona persona = Builder.build(Persona.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setTitle("title"))
            .with(p -> p.setIsSupported(false))
            .with(p -> p.setDescription("dev persona"))
            .get();
    private final PersonaDTO personaDTOExpected = Builder.build(PersonaDTO.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setTitle("title"))
            .with(p -> p.setIsSupported(false))
            .with(p -> p.setDescription("dev persona"))
            .with(p -> p.setCreationDate(persona.getCreationDate()))
            .get();

    @Test
    void should_have_all_personaDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Persona.class, fields::add);

        assertThat(fields).hasSize(PersonaDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Persona persona2 = new Persona();
        BeanUtils.copyProperties(persona, persona2);

        assertThat(persona).isEqualTo(persona);
        assertThat(persona).isNotNull();
        assertThat(persona).isNotEqualTo(new User());
        assertThat(persona).isNotSameAs(new Persona());
        assertThat(persona).isEqualTo(persona2);
    }

    @Test
    void should_get_properties() {
        assertThat(persona.getId()).isEqualTo(1L);
        assertThat(persona.getTitle()).isEqualTo("title");
        assertThat(persona.getIsSupported()).isFalse();
        assertThat(persona.getDescription()).isEqualTo("dev persona");
    }

    @Test
    void can_return_dto() {
        assertThat(persona.toDto()).isEqualTo(personaDTOExpected);
    }
}
