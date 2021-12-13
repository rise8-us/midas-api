package mil.af.abms.midas.api.measure;


import static mil.af.abms.midas.api.helper.TimeConversion.getLocalDateOrNullFromObject;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
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
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.measure.dto.MeasurableDTO;
import mil.af.abms.midas.api.measure.dto.MeasureDTO;
import mil.af.abms.midas.api.measure.dto.UpdateMeasureDTO;
import mil.af.abms.midas.enums.ProgressionStatus;

@Slf4j
@Service
public class MeasureService extends AbstractCRUDService<Measure, MeasureDTO, MeasureRepository> {

    private static final UnaryOperator<String> UPDATE_TOPIC = topic -> "/topic/update_" + topic.toLowerCase();

    private AssertionService assertionService;
    private CommentService commentService;
    private final SimpMessageSendingOperations websocket;

    public MeasureService(MeasureRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Measure.class, MeasureDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setAssertionService(AssertionService assertionService) { this.assertionService = assertionService; }

    @Autowired
    public void setCommentService(CommentService commentService) { this.commentService = commentService; }

    @Transactional
    public Measure create(CreateMeasureDTO dto) {
        Measure newMeasure = Builder.build(Measure.class)
                .with(m -> m.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate())))
                .with(m -> m.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate())))
                .with(m -> m.setCompletedAt(calculateCompletedAt(dto, m)))
                .with(m -> m.setStatus(ProgressionStatus.NOT_STARTED))
                .with(m -> m.setCompletionType(dto.getCompletionType()))
                .with(m -> m.setValue(dto.getValue()))
                .with(m -> m.setTarget(dto.getTarget()))
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

        foundMeasure.setStatus(dto.getStatus());
        foundMeasure.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate()));
        foundMeasure.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate()));
        foundMeasure.setCompletedAt(calculateCompletedAt(dto, foundMeasure));
        foundMeasure.setCompletionType(dto.getCompletionType());
        foundMeasure.setValue(dto.getValue());
        foundMeasure.setTarget(dto.getTarget());
        foundMeasure.setText(dto.getText());

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

    private void removeRelatedComments(Measure measure) {
        measure.getComments().forEach(commentService::deleteComment);
        measure.setComments(Set.of());
    }

    private void removeRelationIfExists(Assertion assertion, Measure measure) {
        Optional.ofNullable(assertion).map(a -> {
            var measures = a.getMeasures().stream().filter(m -> !m.equals(measure)).collect(Collectors.toSet());
            a.setMeasures(measures);
            return a;
        }).ifPresent(a -> websocket.convertAndSend(UPDATE_TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

    private void updateRelation(Assertion assertion, Measure measure) {
        Optional.ofNullable(assertion).map(a -> {
            a.getMeasures().add(measure);
            return a;
        }).ifPresent(a -> websocket.convertAndSend(UPDATE_TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

    private LocalDateTime calculateCompletedAt(MeasurableDTO dto, Measure measure) {
        var target = dto.getTarget() != null ? dto.getTarget() : 0F;
        var value = dto.getValue() != null ? dto.getValue() : 0F;
        var isNewlyCompleted = value >= target && measure.getCompletedAt() == null;

        if (target == 0F) return null;
        if (isNewlyCompleted) return LocalDateTime.now();
        if (target > value) return null;

        return measure.getCompletedAt();
    }
}
