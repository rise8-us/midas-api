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
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.CompletionType;
import mil.af.abms.midas.enums.ProgressionStatus;

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
    UserService userService;
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
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(3L))
            .get();
    private Measure measure = new Measure();
    private CreateMeasureDTO createMeasureDTO = new CreateMeasureDTO();
    private UpdateMeasureDTO updateMeasureDTO = new UpdateMeasureDTO();
    private final Comment comment = Builder.build(Comment.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setText("comment text"))
            .with(c -> c.setMeasure(measure))
            .get();

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
                .with(m -> m.setStatus(ProgressionStatus.NOT_STARTED))
                .get();
       this.createMeasureDTO = new CreateMeasureDTO(
                0F,
                5F,
                measure.getText(),
                assertion.getId(),
                measure.getStatus(),
                measure.getStartDate().toString(),
                measure.getDueDate().toString(),
                measure.getCompletionType()
        );
        this.updateMeasureDTO = new UpdateMeasureDTO(
                measure.getTarget(),
                5F,
                "Updated",
                measure.getStatus(),
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
        assertThat(measureSaved.getStatus()).isEqualTo(createMeasureDTO.getStatus());
        assertThat(assertion.toDto()).isEqualTo(assertionCaptor.getValue());
        assertThat("/topic/update_assertion").isEqualTo(stringCaptor.getValue());
    }

    @Test
    void should_deleteMeasure() {
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(5L)).get();
        measure.getComments().add(comment);
        assertion.getMeasures().add(measure);

        doReturn(measure).when(measureService).findById(measure.getId());
        doNothing().when(repository).deleteById(any());

        measureService.deleteMeasure(measure);
        verify(repository, times(1)).deleteById(longCaptor.capture());
        verify(commentService, times(1)).deleteAllRelatedComments(any());

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(1L);
    }

    @Test
    void should_updateMeasureIfAssertionComplete() {
        when(userService.getUserDisplayNameOrUsername()).thenReturn("User");
        when(commentService.create(any(), any())).thenReturn(comment);

        measureService.updateMeasureIfAssertionComplete(measure, ProgressionStatus.COMPLETED, "foo");
        verify(repository, times(1)).save(measureCaptor.capture());

        var measureSaved = measureCaptor.getValue();

        assertThat(measureSaved.getStatus()).isEqualTo(ProgressionStatus.COMPLETED);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1: 5: 5: ON_TRACK: ON_TRACK: COMPLETED",
            "1: 1: 5: ON_TRACK: COMPLETED: COMPLETED",
            "1: 1: 1: NOT_STARTED: ON_TRACK: ON_TRACK",
            "2: 3: 3: ON_TRACK: ON_TRACK: BLOCKED",
            "5: 0: 0: COMPLETED: COMPLETED: NOT_STARTED",
            "0: 1: 1: NOT_STARTED: NOT_STARTED: ON_TRACK",
            "0: 2: 2: NOT_STARTED: NOT_STARTED: ON_TRACK"
    }, delimiter = ':')
    void should_updateById_and_calculateValueAndStatus(
            Float initialValue, Float updatedValue, Float expectedValue,
            String initialStatus, String updatedStatus, String expectedStatus) {

        var date = LocalDate.now();

        measure.setValue(initialValue);
        measure.setStatus(ProgressionStatus.valueOf(initialStatus));
        if (initialStatus.equals("COMPLETED")) measure.setCompletedAt(LocalDateTime.now());
        updateMeasureDTO.setValue(updatedValue);
        updateMeasureDTO.setStatus(ProgressionStatus.valueOf(updatedStatus));
        updateMeasureDTO.setStartDate(updatedValue.equals(2F) ? null : date.toString());
        updateMeasureDTO.setDueDate(initialValue.equals(2F) ? date.minusDays(1).toString() : date.plusDays(1).toString());

        doReturn(measure).when(measureService).findById(measure.getId());
        when(repository.save(any(Measure.class))).thenReturn(new Measure());

        measureService.updateById(measure.getId(), updateMeasureDTO);
        verify(repository, times(1)).save(measureCaptor.capture());

        var measureSaved = measureCaptor.getValue();

        assertThat(measureSaved.getStartDate()).isNotNull();
        assertThat(measureSaved.getDueDate()).isEqualTo(updateMeasureDTO.getDueDate());
        assertThat(measureSaved.getCompletionType()).isEqualTo(updateMeasureDTO.getCompletionType());
        assertThat(measureSaved.getValue()).isEqualTo(expectedValue);
        assertThat(measureSaved.getTarget()).isEqualTo(updateMeasureDTO.getTarget());
        assertThat(measureSaved.getText()).isEqualTo(updateMeasureDTO.getText());
        assertThat(measureSaved.getStatus()).isEqualTo(ProgressionStatus.valueOf(expectedStatus));
    }

}
