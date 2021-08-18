package mil.af.abms.midas.api.feature;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.feature.dto.CreateFeatureDTO;
import mil.af.abms.midas.api.feature.dto.UpdateFeatureDTO;
import mil.af.abms.midas.api.helper.Builder;

@WebMvcTest({FeatureController.class})
class FeatureControllerTests extends ControllerTestHarness {

    @MockBean
    private FeatureService featureService;

    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final UpdateFeatureDTO updateFeatureDTO = new UpdateFeatureDTO("JICO", "warfighter", 0, 2L);
    private final CreateFeatureDTO createFeatureDTO = new CreateFeatureDTO("JICO", "warfighter", 2L, 1);
    private final Feature feature = Builder.build(Feature.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setTitle(createFeatureDTO.getTitle()))
            .with(t -> t.setDescription(createFeatureDTO.getDescription())).get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_feature() throws Exception {
        when(featureService.create(any(CreateFeatureDTO.class))).thenReturn(feature);

        mockMvc.perform(post("/api/features")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createFeatureDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(feature.getTitle()));
    }

    @Test
    void should_update_feature_by_id() throws Exception {
        when(featureService.updateById(anyLong(), any(UpdateFeatureDTO.class))).thenReturn(feature);

        mockMvc.perform(put("/api/features/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateFeatureDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(feature.getTitle()));
    }

    @Test
    void should_bulk_update_feature() throws Exception {
        when(featureService.bulkUpdate(any())).thenReturn(List.of(feature));

        mockMvc.perform(put("/api/features/bulk")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(List.of(updateFeatureDTO)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].title").value(feature.getTitle()));
    }

}
