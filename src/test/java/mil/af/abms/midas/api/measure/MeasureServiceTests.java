package mil.af.abms.midas.api.measure;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.springframework.beans.BeanUtils;
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
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.completion.CompletionService;
import mil.af.abms.midas.api.completion.dto.CreateCompletionDTO;
import mil.af.abms.midas.api.completion.dto.UpdateCompletionDTO;
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
    CompletionService completionService;
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

    private final LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2021-07-09");
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(3L))
            .get();
    private final Completion completion = Builder.build(Completion.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setValue(1F))
            .with(c -> c.setTarget(5F))
            .with(c -> c.setCompletionType(CompletionType.NUMBER))
            .with(c -> c.setDueDate(DUE_DATE))
            .with(c -> c.setStartDate(DUE_DATE))
            .get();
    private final UpdateCompletionDTO updateCompletionDTO = new UpdateCompletionDTO(
            completion.getStartDate().toString(),
            completion.getDueDate().toString(),
            completion.getCompletionType(),
            0F,
            1F,
            null,
            null
    );
    private final CreateCompletionDTO createCompletionDTO = new CreateCompletionDTO(
            completion.getStartDate().toString(),
            completion.getDueDate().toString(),
            null,
            completion.getCompletionType(),
            0F,
            5F,
            null,
            null

    );
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
                .with(m -> m.setText("First"))
                .with(m -> m.setAssertion(assertion))
                .with(m -> m.setStatus(ProgressionStatus.NOT_STARTED))
                .with(m -> m.setCompletion(completion))
                .get();
       this.createMeasureDTO = new CreateMeasureDTO(
                measure.getText(),
                assertion.getId(),
                measure.getStatus(),
                createCompletionDTO
        );
        this.updateMeasureDTO = new UpdateMeasureDTO(
                "Updated",
                measure.getStatus(),
                updateCompletionDTO

        );
    }

    @Test
    void should_create_measure() {
        doReturn(completion).when(completionService).create(createCompletionDTO);
        doReturn(measure).when(repository).save(any());
        measureService.create(createMeasureDTO);

        verify(repository, times(1)).save(any());
        verify(measureService, times(1)).updateRelation(any(), any());
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
    void should_deleteMeasure_without_removing_assertion_relationship() {
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(5L)).get();

        Measure measure2 = new Measure();
        BeanUtils.copyProperties(measure, measure2);
        measure2.setId(2L);
        measure2.getComments().add(comment);
        assertion.getMeasures().add(measure);

        doReturn(measure2).when(measureService).findById(measure2.getId());
        doNothing().when(repository).deleteById(any());

        measureService.deleteMeasure(measure2);
        verify(repository, times(1)).deleteById(longCaptor.capture());
        verify(commentService, times(1)).deleteAllRelatedComments(any());

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(2L);
    }

    @ParameterizedTest
    @CsvSource(value = {
            "COMPLETED: COMPLETED",
            "NOT_STARTED: NOT_STARTED",
            "COMPLETED: NOT_STARTED",
            "NOT_STARTED: COMPLETED"
    }, delimiter = ':')
    void should_updateMeasureIfAssertionComplete(String assertionStatus, String measureStatus) {
        when(userService.getUserDisplayNameOrUsername()).thenReturn("User");
        when(commentService.create(any(), any())).thenReturn(comment);

        measure.setStatus(ProgressionStatus.valueOf(measureStatus));

        measureService.updateMeasureIfAssertionComplete(measure, ProgressionStatus.valueOf(assertionStatus), "foo");

        if (assertionStatus.equals("COMPLETED") && measureStatus.equals("NOT_STARTED")) {
            verify(repository, times(1)).save(measureCaptor.capture());

            var measureSaved = measureCaptor.getValue();

            assertThat(measureSaved.getStatus()).isEqualTo(ProgressionStatus.COMPLETED);
        } else {
            verifyNoInteractions(repository);
        }
    }

    @Test
    void should_update_by_id() {
        doReturn(measure).when(measureService).findById(anyLong());
        doReturn(completion).when(completionService).updateById(anyLong(), any());
        doNothing().when(assertionService).updateAssertionIfAllChildrenAndMeasuresComplete(any());

        measureService.updateById(1L, updateMeasureDTO);
        verify(repository, times(1)).save(any());
    }

    @ParameterizedTest
    @CsvSource(value = { "COMPLETED", "NOT_STARTED" })
    void should_calculate_value(String status) {
        updateMeasureDTO.setStatus(ProgressionStatus.valueOf(status));

        doNothing().when(commentService).createSystemComment(anyLong(), anyLong(), anyString());

        measureService.calculateValue(updateMeasureDTO, measure);
        verify(measureService, times(1)).calculateValue(any(), any());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "1: 5: ON_TRACK: COMPLETED: 0",
            "5: 5: ON_TRACK: ON_TRACK: 0",
            "5: 5: COMPLETED: COMPLETED: 0",
            "1: 5: ON_TRACK: ON_TRACK: 0",
            "2: 5: AT_RISK: AT_RISK: 0",
            "1: 5: AT_RISK: AT_RISK: 0",
            "0: 5: ON_TRACK: ON_TRACK: 1",
            "0: 5: BLOCKED: BLOCKED: 1",
            "0: 5: ON_TRACK: ON_TRACK: 0",
            "0: 5: NOT_STARTED: NOT_STARTED: 0",
    }, delimiter = ':')
    void should_calculate_status(Float value, Float target, String measureStatus, String dtoStatus, Integer daysToAdd) {
        var foundCompletion = measure.getCompletion();
        foundCompletion.setValue(value);
        foundCompletion.setTarget(target);
        measure.setStatus(ProgressionStatus.valueOf(measureStatus));
        updateMeasureDTO.setStatus(ProgressionStatus.valueOf(dtoStatus));
        foundCompletion.setDueDate(LocalDate.now().minusDays(daysToAdd));

        if (value == 2F || value == 0F) {
            foundCompletion.setStartDate(null);
        }

        doNothing().when(commentService).createSystemComment(anyLong(), anyLong(), anyString());

        measureService.calculateStatus(updateMeasureDTO, measure);
        verify(measureService, times(1)).calculateStatus(any(), any());
    }

}
