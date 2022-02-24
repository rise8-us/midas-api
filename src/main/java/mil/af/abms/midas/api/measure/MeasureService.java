package mil.af.abms.midas.api.measure;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.comment.SystemComments;
import mil.af.abms.midas.api.completion.CompletionService;
import mil.af.abms.midas.api.completion.dto.UpdateCompletionDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.measure.dto.MeasureDTO;
import mil.af.abms.midas.api.measure.dto.UpdateMeasureDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Slf4j
@Service
public class MeasureService extends AbstractCRUDService<Measure, MeasureDTO, MeasureRepository> {

    private static final UnaryOperator<String> UPDATE_TOPIC = topic -> "/topic/update_" + topic.toLowerCase();

    private AssertionService assertionService;
    private CommentService commentService;
    private CompletionService completionService;
    private final SimpMessageSendingOperations websocket;

    public MeasureService(MeasureRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Measure.class, MeasureDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setAssertionService(AssertionService assertionService) { this.assertionService = assertionService; }
    @Autowired
    public void setCommentService(CommentService commentService) { this.commentService = commentService; }
    @Autowired
    public void setCompletionService(CompletionService completionService) { this.completionService = completionService; }

    @Transactional
    public Measure create(CreateMeasureDTO dto) {
        var completion = completionService.create(dto.getCompletion());
        Measure newMeasure = Builder.build(Measure.class)
                .with(m -> m.setCompletion(completion))
                .with(m -> m.setStatus(ProgressionStatus.NOT_STARTED))
                .with(m -> m.setText(dto.getText()))
                .with(m -> m.setAssertion(this.assertionService.findById(dto.getAssertionId())))
                .get();

        var savedMeasure = repository.save(newMeasure);
        updateRelation(savedMeasure.getAssertion(), savedMeasure);

        return savedMeasure;
    }

    @Transactional
    public Measure updateById(Long id, UpdateMeasureDTO dto) {
        var foundMeasure = findById(id);
        var completionId = foundMeasure.getCompletion().getId();

        UpdateCompletionDTO updateCompletionDTO = dto.getCompletion();
        updateCompletionDTO.setValue(calculateValue(dto, foundMeasure));
        completionService.updateById(completionId, dto.getCompletion());

        foundMeasure.setText(dto.getText());
        foundMeasure.setStatus(calculateStatus(dto, foundMeasure));

        assertionService.updateAssertionIfAllChildrenAndMeasuresComplete(foundMeasure.getAssertion());

        return repository.save(foundMeasure);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Measure measureToDelete = findById(id);
        removeRelationIfExists(measureToDelete.getAssertion(), measureToDelete);
        removeRelatedComments(measureToDelete);
        repository.deleteById(id);
    }

    @Transactional
    public void deleteMeasure(Measure measure) {
        deleteById(measure.getId());
    }

    public void updateMeasureIfAssertionComplete(Measure measure, ProgressionStatus assertionStatus, String assertionTitle) {
        if (getStatusIsCompletedAndMeasureNotCompleted(assertionStatus, measure)) {
            completionService.setCompletedAtAndValueToTarget(measure.getCompletion().getId());
            measure.setStatus(ProgressionStatus.COMPLETED);
            var text = SystemComments.ASSERTION_COMPLETE.apply(assertionTitle, measure.getText());

            commentService.createSystemComment(null, measure.getId(), text);

            repository.save(measure);
        }
    }

    private boolean getStatusIsCompletedAndMeasureNotCompleted(ProgressionStatus status, Measure measure) {
        return ProgressionStatus.COMPLETED.equals(status) && measure.getStatus() != ProgressionStatus.COMPLETED;
    }

    private void removeRelatedComments(Measure measure) {
        measure.getComments().forEach(commentService::deleteAllRelatedComments);
        measure.setComments(Set.of());
    }

    private void removeRelationIfExists(Assertion assertion, Measure measure) {
        Optional.ofNullable(assertion).map(a -> {
            var measures = a.getMeasures().stream().filter(m -> !m.equals(measure)).collect(Collectors.toSet());
            a.setMeasures(measures);
            return a;
        }).ifPresent(a -> websocket.convertAndSend(UPDATE_TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

    protected void updateRelation(Assertion assertion, Measure measure) {
        Optional.ofNullable(assertion).map(a -> {
            a.getMeasures().add(measure);
            return a;
        }).ifPresent(a -> websocket.convertAndSend(UPDATE_TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

    protected Float calculateValue(UpdateMeasureDTO dto, Measure measure) {
        if (dto.getStatus().equals(ProgressionStatus.COMPLETED)) {
            String text = SystemComments.MEASURE_STATUS_SET_COMPLETED.apply(measure.getText());
            commentService.createSystemComment(null, measure.getId(), text);
            return measure.getCompletion().getTarget();
        }
        return dto.getCompletion().getValue();
    }

    protected ProgressionStatus calculateStatus(UpdateMeasureDTO dto, Measure measure) {
        if (!dto.getStatus().equals(measure.getStatus())) return dto.getStatus();

        if (measure.getCompletion().getValue().equals(measure.getCompletion().getTarget())) {
            commentWhenNewlyCompleted(measure);
            return ProgressionStatus.COMPLETED;
        }

        if (measure.getCompletion().getDueDate() != null && measure.getCompletion().getDueDate().compareTo(LocalDate.now()) < 0) {
            commentWhenNewlyBlocked(measure);
            return ProgressionStatus.BLOCKED;
        }

        if (measure.getCompletion().getValue().equals(0F) && measure.getCompletion().getStartDate() == null) {
            commentWhenNewlyNotStarted(measure);
            return ProgressionStatus.NOT_STARTED;
        }

        commentWhenNewlyOnTrack(measure);
        return ProgressionStatus.ON_TRACK;
    }

    protected void commentWhenNewlyCompleted(Measure measure) {
        if (!measure.getStatus().equals(ProgressionStatus.COMPLETED)) {
            String text = SystemComments.MEASURE_VALUE_MET_TARGET.apply(measure.getText());
            commentService.createSystemComment(null, measure.getId(), text);
        }
    }

    protected void commentWhenNewlyBlocked(Measure measure) {
        if (!measure.getStatus().equals(ProgressionStatus.BLOCKED)) {
            String text = SystemComments.MEASURE_PAST_DUE.apply(measure.getText());
            commentService.createSystemComment(null, measure.getId(), text);
        }
    }

    protected void commentWhenNewlyNotStarted(Measure measure) {
        if (!measure.getStatus().equals(ProgressionStatus.NOT_STARTED)) {
            String text = SystemComments.MEASURE_VALUE_SET_TO_ZERO.apply(measure.getText());
            commentService.createSystemComment(null, measure.getId(), text);
        }
    }

    protected void commentWhenNewlyOnTrack(Measure measure) {
        if (!measure.getStatus().equals(ProgressionStatus.ON_TRACK)) {
            String text;
            if (measure.getCompletion().getStartDate() == null) {
                completionService.setStartDate(measure.getCompletion().getId(), LocalDate.now());
                text = SystemComments.MEASURE_ON_TRACK_SET_START_DATE.apply(measure.getText());
            } else {
                text = SystemComments.MEASURE_ON_TRACK.apply(measure.getText());
            }
            commentService.createSystemComment(null, measure.getId(), text);
        }
    }

}
