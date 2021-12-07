package mil.af.abms.midas.api.measure;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.measure.dto.UpdateMeasureDTO;
import mil.af.abms.midas.enums.CompletionType;

@ExtendWith(SpringExtension.class)
@Import(MeasureService.class)
class MeasureServiceTests {

    @SpyBean
    private MeasureService measureService;
    @MockBean
    private MeasureRepository repository;
    @MockBean
    private AssertionService assertionService;
    @MockBean
    CommentService commentService;
    @MockBean
    SimpMessageSendingOperations websocket;

    @Captor
    private ArgumentCaptor<Measure> measureCaptor;
    @Captor
    private ArgumentCaptor<Assertion> assertionCaptor;
    @Captor
    private ArgumentCaptor<Long> longCaptor;
    @Captor
    private ArgumentCaptor<String> stringCaptor;


    private final LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2021-07-09");
    private final LocalDateTime COMPLETED_DATE = LocalDateTime.now();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(3L))
            .get();
    private Measure measure = new Measure();
    private CreateMeasureDTO createMeasureDTO = new CreateMeasureDTO();
    private UpdateMeasureDTO updateMeasureDTO = new UpdateMeasureDTO();


    @BeforeEach
    void init() {
        this.measure = Builder.build(Measure.class)
                .with(m -> m.setId(1L))
                .with(m -> m.setStartDate(DUE_DATE))
                .with(m -> m.setDueDate(DUE_DATE))
                .with(m -> m.setCompletionType(CompletionType.NUMBER))
                .with(m -> m.setValue(1F))
                .with(m -> m.setTarget(5F))
                .with(m -> m.setText("First"))
                .with(m -> m.setAssertion(assertion))
                .get();
       this.createMeasureDTO = new CreateMeasureDTO(
                0F,
                5F,
                measure.getText(),
                assertion.getId(),
                measure.getStartDate().toString(),
                measure.getDueDate().toString(),
                measure.getCompletionType()
        );
        this.updateMeasureDTO = new UpdateMeasureDTO(
                measure.getTarget(),
                5F,
                "Updated",
                measure.getStartDate().toString(),
                measure.getDueDate().toString(),
                measure.getCompletionType()
        );
    }

    @ParameterizedTest
    @CsvSource(value = {" , ", "1F,2F", "2F, 2F"})
    void should_create_measure(Float value, Float target) {
        createMeasureDTO.setValue(value);
        createMeasureDTO.setTarget(target);

        when(repository.save(any())).thenReturn(measure);
        when(assertionService.findById(assertion.getId())).thenReturn(assertion);

        measureService.create(createMeasureDTO);
        verify(repository, times(1)).save(measureCaptor.capture());
        verify(websocket, times(1)).convertAndSend(stringCaptor.capture(), assertionCaptor.capture());
        Measure measureSaved = measureCaptor.getAllValues().get(0);

        assertThat(measureSaved.getText()).isEqualTo(createMeasureDTO.getText());
        assertThat(measureSaved.getCompletionType()).isEqualTo(createMeasureDTO.getCompletionType());
        assertThat(measureSaved.getStartDate()).isEqualTo(createMeasureDTO.getStartDate());
        assertThat(measureSaved.getDueDate()).isEqualTo(createMeasureDTO.getDueDate());
        assertThat(measureSaved.getValue()).isEqualTo(createMeasureDTO.getValue());
        assertThat(measureSaved.getTarget()).isEqualTo(createMeasureDTO.getTarget());
        assertThat(measureSaved.getText()).isEqualTo(createMeasureDTO.getText());
        assertThat(assertion.toDto()).isEqualTo(assertionCaptor.getValue());
        assertThat("/topic/update_assertion").isEqualTo(stringCaptor.getValue());
    }

    @Test
    void should_delete() {
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(5L)).get();
        measure.getComments().add(comment);
        assertion.getMeasures().add(measure);

        doReturn(measure).when(measureService).findById(measure.getId());
        doNothing().when(repository).deleteById(any());

        measureService.deleteMeasure(measure);
        verify(repository, times(1)).deleteById(longCaptor.capture());
        verify(commentService, times(1)).deleteComment(any());

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(1L);
    }

    @ParameterizedTest
    @CsvSource(value = {"5, 2021-10-01T00:00:00", "1, 2021-10-01T00:00:00", "3, 2021-10-01T00:00:00"})
    void should_update_measure_and_set_complete_if_value_matches_target(Float value, String date) {
        var dateTime = TimeConversion.getTime(date);
        var expectedDateTime = value >= 2 ? dateTime : null;
        updateMeasureDTO.setValue(value);
        measure.setCompletedAt(!value.equals(3F) ? dateTime : null);

        doReturn(measure).when(measureService).findById(measure.getId());
        when(repository.save(any(Measure.class))).thenReturn(new Measure());

        measureService.updateById(measure.getId(), updateMeasureDTO);
        verify(repository, times(1)).save(measureCaptor.capture());

        var measureSaved = measureCaptor.getValue();

        assertThat(measureSaved.getStartDate()).isEqualTo(updateMeasureDTO.getStartDate());
        assertThat(measureSaved.getDueDate()).isEqualTo(updateMeasureDTO.getDueDate());
        assertThat(measureSaved.getCompletionType()).isEqualTo(updateMeasureDTO.getCompletionType());
        assertThat(measureSaved.getValue()).isEqualTo(updateMeasureDTO.getValue());
        assertThat(measureSaved.getTarget()).isEqualTo(updateMeasureDTO.getTarget());
        assertThat(measureSaved.getText()).isEqualTo(updateMeasureDTO.getText());

        if (value.equals(5F)) {
            assertThat(measureSaved.getCompletedAt()).isAfterOrEqualTo(expectedDateTime);
        } else {
            assertThat(measureSaved.getCompletedAt()).isNull();
        }
    }

}
