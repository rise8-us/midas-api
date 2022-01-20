package mil.af.abms.midas.api.persona;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.persona.dto.CreatePersonaDTO;
import mil.af.abms.midas.api.persona.dto.UpdatePersonaDTO;

@WebMvcTest({PersonaController.class})
class PersonaControllerTests extends ControllerTestHarness {

    @MockBean
    private PersonaService personaService;

    private final UpdatePersonaDTO updatePersonaDTO = new UpdatePersonaDTO("JICO", "warfighter", true, 0, 1L);
    private final CreatePersonaDTO createPersonaDTO = new CreatePersonaDTO("JICO", "warfighter", 2L, true, 1);
    private final Persona persona = Builder.build(Persona.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setTitle(createPersonaDTO.getTitle()))
            .with(t -> t.setIsSupported(false))
            .with(t -> t.setDescription(createPersonaDTO.getDescription())).get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_persona() throws Exception {
        when(personaService.create(any(CreatePersonaDTO.class))).thenReturn(persona);

        mockMvc.perform(post("/api/personas")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createPersonaDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(persona.getTitle()));
    }

    @Test
    void should_update_persona_by_id() throws Exception {
        when(personaService.updateById(anyLong(), any(UpdatePersonaDTO.class))).thenReturn(persona);

        mockMvc.perform(put("/api/personas/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updatePersonaDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(persona.getTitle()));
    }


    @Test
    void should_bulk_update_persona() throws Exception {
        when(personaService.bulkUpdate(any())).thenReturn(List.of(persona));

        mockMvc.perform(put("/api/personas/bulk")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(List.of(updatePersonaDTO)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].title").value(persona.getTitle()));
    }

    @Test
    public void should_delete_persona_by_id() throws Exception {
        doNothing().when(personaService).deleteById(any());
        when(personaService.existsById(55L)).thenReturn(true);

        mockMvc.perform(delete("/api/personas/1"))
                .andExpect(status().isOk());
    }

}
