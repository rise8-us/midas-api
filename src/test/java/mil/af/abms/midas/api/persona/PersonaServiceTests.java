package mil.af.abms.midas.api.persona;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.persona.dto.CreatePersonaDTO;
import mil.af.abms.midas.api.persona.dto.UpdatePersonaDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;

@ExtendWith(SpringExtension.class)
@Import(PersonaService.class)
class PersonaServiceTests {

    @SpyBean
    PersonaService personaService;
    @MockBean
    ProductService productService;
    @MockBean
    PersonaRepository personaRepository;
    @Captor
    ArgumentCaptor<Persona> personaCaptor;

    Product product = Builder.build(Product.class)
            .with(p -> p.setId(4L))
            .get();
    Persona persona = Builder.build(Persona.class)
            .with(p -> p.setTitle("MIDAS"))
            .with(p -> p.setIsSupported(true))
            .with(p -> p.setDescription("dev persona"))
            .with(p -> p.setId(1L))
            .with(p -> p.setPosition(0))
            .with(p -> p.setProduct(product))
            .get();

    @Test
    void should_create_persona() {
        CreatePersonaDTO createPersonaDTO = new CreatePersonaDTO("MIDAS", "dev persona", 4L, true, 0);

        when(productService.findById(createPersonaDTO.getProductId())).thenReturn(product);
        when(personaRepository.save(persona)).thenReturn(new Persona());

        personaService.create(createPersonaDTO);

        verify(personaRepository, times(1)).save(personaCaptor.capture());
        Persona personaSaved = personaCaptor.getValue();

        assertThat(personaSaved.getTitle()).isEqualTo(createPersonaDTO.getTitle());
        assertThat(personaSaved.getIsSupported()).isEqualTo(createPersonaDTO.getIsSupported());
        assertThat(personaSaved.getDescription()).isEqualTo(createPersonaDTO.getDescription());
        assertThat(personaSaved.getPosition()).isEqualTo(createPersonaDTO.getIndex());
        assertThat(personaSaved.getProduct().getId()).isEqualTo(product.getId());
    }

    @Test
    void should_update_persona_by_id() {
        UpdatePersonaDTO updatePersonaDTO = new UpdatePersonaDTO("Home One", "dev persona", false, 1, 1L);

        when(personaRepository.findById(1L)).thenReturn(Optional.of(persona));
        when(personaRepository.save(persona)).thenReturn(persona);

        personaService.updateById(1L, updatePersonaDTO);

        verify(personaRepository, times(1)).save(personaCaptor.capture());
        Persona personaSaved = personaCaptor.getValue();

        assertThat(personaSaved.getTitle()).isEqualTo(updatePersonaDTO.getTitle());
        assertThat(personaSaved.getIsSupported()).isEqualTo(updatePersonaDTO.getIsSupported());
        assertThat(personaSaved.getDescription()).isEqualTo(updatePersonaDTO.getDescription());
        assertThat(personaSaved.getPosition()).isEqualTo(updatePersonaDTO.getIndex());
    }

    @Test
    void should_bulk_update_persona() {
        UpdatePersonaDTO updatePersonaDTO = new UpdatePersonaDTO(
                "Home One", "dev persona",  true, 1, 1L
        );

        doReturn(persona).when(personaService).updateById(1L, updatePersonaDTO);

        personaService.bulkUpdate(List.of(updatePersonaDTO));
        verify(personaService, times(1)).updateById(1L, updatePersonaDTO);

    }

}
