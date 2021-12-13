package mil.af.abms.midas.api.measure;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.measure.dto.UpdateMeasureDTO;
import mil.af.abms.midas.enums.CompletionType;
import mil.af.abms.midas.enums.ProgressionStatus;

@WebMvcTest({MeasureController.class})
public class MeasureControllerTests extends ControllerTestHarness {
    
    @MockBean
    private MeasureService measureService;
    @MockBean
    private AssertionService assertionService;

    private final LocalDate START_DATE = TimeConversion.getLocalDateOrNullFromObject("2020-01-01");
    private final LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2020-02-02");
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(3L))
            .get();
    private final Measure measure = Builder.build(Measure.class)
            .with(m -> m.setId(1L))
            .with(m -> m.setStartDate(START_DATE))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setValue(1F))
            .with(m -> m.setTarget(5F))
            .with(m -> m.setText("First"))
            .with(m -> m.setAssertion(assertion))
            .with(m -> m.setCompletionType(CompletionType.NUMBER))
            .with(m -> m.setStatus(ProgressionStatus.NOT_STARTED))
            .get();
    CreateMeasureDTO createMeasureDTO = new CreateMeasureDTO(
            measure.getValue(),
            measure.getTarget(),
            measure.getText(),
            assertion.getId(),
            measure.getStatus(),
            measure.getStartDate().toString(),
            measure.getDueDate().toString(),
            measure.getCompletionType()
    );
    UpdateMeasureDTO updateMeasureDTO = new UpdateMeasureDTO(
            measure.getValue(),
            measure.getTarget(),
            "Updated",
            measure.getStatus(),
            measure.getStartDate().toString(),
            measure.getDueDate().toString(),
            measure.getCompletionType()
    );

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_measure() throws Exception {
        when(measureService.create(any(CreateMeasureDTO.class))).thenReturn(measure);
        when(assertionService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/measures")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createMeasureDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value("First"));
    }

    @Test
    void should_update_by_id() throws Exception {
        var newMeasure = new Measure();
        BeanUtils.copyProperties(updateMeasureDTO, newMeasure);
        when(measureService.updateById(any(), any(UpdateMeasureDTO.class))).thenReturn(newMeasure);

        mockMvc.perform(put("/api/measures/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateMeasureDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value("Updated"));
    }

    @Test
    void should_throw_text_must_not_be_blank_message_on_create() throws Exception {
        var measure2 = new CreateMeasureDTO();
        BeanUtils.copyProperties(createMeasureDTO, measure2);
        measure2.setValue(null);
        measure2.setTarget(null);
        measure2.setText(null);

        when(assertionService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/measures")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(measure2))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").exists())
                .andExpect(jsonPath("$.errors[1]").exists())
                .andExpect(jsonPath("$.errors[2]").exists())
                .andExpect(jsonPath("$.errors[3]").doesNotExist());
    }

    @Test
    void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/measures/1"))
                .andExpect(status().isOk());

        verify(measureService, times(1)).deleteById(1L);
    }

}
