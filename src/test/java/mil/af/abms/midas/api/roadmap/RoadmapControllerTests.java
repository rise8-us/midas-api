package mil.af.abms.midas.api.roadmap;

import static org.mockito.ArgumentMatchers.any;
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
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.roadmap.dto.CreateRoadmapDTO;
import mil.af.abms.midas.api.roadmap.dto.UpdateRoadmapDTO;
import mil.af.abms.midas.enums.RoadmapStatus;

@WebMvcTest({RoadmapController.class})
class RoadmapControllerTests extends ControllerTestHarness {

    @MockBean
    private RoadmapService roadmapService;

    private final static LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final UpdateRoadmapDTO updateRoadmapDTO = new UpdateRoadmapDTO(
            "Do cool things", "awesome stuff", RoadmapStatus.COMPLETE, 0, 1L, "2021-10-10"
    );
    private final CreateRoadmapDTO createRoadmapDTO = new CreateRoadmapDTO(
            "Do things", "stuff", 2L, RoadmapStatus.IN_PROGRESS, 1, "2021-11-10"
    );
    private final Roadmap roadmap = Builder.build(Roadmap.class)
            .with(t -> t.setId(2L))
            .with(t -> t.setTitle(createRoadmapDTO.getTitle()))
            .with(t -> t.setStatus(RoadmapStatus.IN_PROGRESS))
            .with(t -> t.setDescription(createRoadmapDTO.getDescription())).get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_roadmap() throws Exception {
        when(roadmapService.create(any(CreateRoadmapDTO.class))).thenReturn(roadmap);

        mockMvc.perform(post("/api/roadmaps")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(createRoadmapDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.title").value(roadmap.getTitle()));
    }

    @Test
    void should_bulk_update_roadmap() throws Exception {
        when(roadmapService.bulkUpdate(any())).thenReturn(List.of(roadmap));

        mockMvc.perform(put("/api/roadmaps/bulk")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(List.of(updateRoadmapDTO)))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$[0].title").value(roadmap.getTitle()));
    }

}
