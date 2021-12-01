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

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private final Measure measure = Builder.build(Measure.class)
            .with(m -> m.setId(1L))
            .with(m -> m.setStartDate(DUE_DATE))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setCompletionType(CompletionType.NUMBER))
            .with(m -> m.setValue(1F))
            .with(m -> m.setTarget(5F))
            .with(m -> m.setText("First"))
            .with(m -> m.setAssertion(assertion))
            .get();
    CreateMeasureDTO createMeasureDTO = new CreateMeasureDTO(
            5F,
            5F,
            measure.getText(),
            assertion.getId(),
            measure.getStartDate().toString(),
            measure.getDueDate().toString(),
            measure.getCompletionType()
    );
    UpdateMeasureDTO updateMeasureDTO = new UpdateMeasureDTO(
            measure.getTarget(),
            measure.getTarget(),
            "Updated",
            measure.getStartDate().toString(),
            measure.getDueDate().toString(),
            measure.getCompletionType()
    );

    @Test
    void should_create_measure() {
        when(repository.save(any())).thenReturn(new Measure());

        measureService.create(createMeasureDTO);

        verify(repository, times(1)).save(measureCaptor.capture());
        Measure measureSaved = measureCaptor.getAllValues().get(0);

        assertThat(measureSaved.getText()).isEqualTo(createMeasureDTO.getText());
        assertThat(measureSaved.getCompletionType()).isEqualTo(createMeasureDTO.getCompletionType());
    }

    @Test
    void should_update_measure_by_id() {
        doReturn(measure).when(measureService).findById(measure.getId());

        measureService.updateById(measure.getId(), updateMeasureDTO);

        verify(repository, times(1)).save(measureCaptor.capture());
        var measureSaved = measureCaptor.getValue();

        assertThat(measureSaved.getText()).isEqualTo(updateMeasureDTO.getText());
        assertThat(measureSaved.getValue()).isEqualTo(measure.getTarget());
    }

    @Test
    void should_not_null_completedAt_if_not_present() {
        UpdateMeasureDTO update = new UpdateMeasureDTO(5F, 10F, "Updated", null, null, null);

        doReturn(measure).when(measureService).findById(measure.getId());

        measureService.updateById(measure.getId(), update);

        verify(repository, times(1)).save(measureCaptor.capture());
        var measureSaved = measureCaptor.getValue();

        assertThat(measureSaved.getCompletedAt()).isNull();
    }

    @Test
    void should_not_null_completedAt_no_target() {
        UpdateMeasureDTO update = new UpdateMeasureDTO(5F, 10F, "Updated", null, null, null);
        var measure2 = new Measure();
        BeanUtils.copyProperties(measure, measure2);
        measure2.setTarget(null);

        doReturn(measure2).when(measureService).findById(measure2.getId());

        measureService.updateById(measure2.getId(), update);

        verify(repository, times(1)).save(measureCaptor.capture());
        var measureSaved = measureCaptor.getValue();

        assertThat(measureSaved.getCompletedAt()).isNull();
    }

    @Test
    void should_not_update_date_when_not_null() {
        var measure2 = new Measure();
        BeanUtils.copyProperties(measure, measure2);
        measure2.setStartDate(DUE_DATE);
        measure2.setCompletedAt(COMPLETED_DATE);

        doReturn(measure2).when(measureService).findById(measure2.getId());

        measureService.updateById(measure2.getId(), updateMeasureDTO);

        verify(repository, times(1)).save(measureCaptor.capture());
        var measureSaved = measureCaptor.getValue();

        assertThat(measureSaved.getStartDate()).isEqualTo(measure2.getStartDate());
        assertThat(measureSaved.getCompletedAt()).isEqualTo(measure2.getCompletedAt());
    }

    @Test
    void should_null_completedAt_if_target_changes() {
        UpdateMeasureDTO update = new UpdateMeasureDTO(5F, 10F, "Updated", null, null, null);
        var measure2 = new Measure();
        BeanUtils.copyProperties(measure, measure2);
        measure2.setCompletedAt(COMPLETED_DATE);

        doReturn(measure2).when(measureService).findById(measure2.getId());

        measureService.updateById(measure2.getId(), update);

        verify(repository, times(1)).save(measureCaptor.capture());
        var measureSaved = measureCaptor.getValue();

        assertThat(measureSaved.getCompletedAt()).isNull();
    }

    @Test
    void should_update_relation() {
        measureService.updateRelation(assertion, measure);

        verify(websocket, times(1)).convertAndSend(stringCaptor.capture(), assertionCaptor.capture());

        assertThat(assertion.toDto()).isEqualTo(assertionCaptor.getValue());
        assertThat("/topic/update_Assertion").isEqualTo(stringCaptor.getValue());
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

}
