package mil.af.abms.midas.api.measure;


import static mil.af.abms.midas.api.helper.TimeConversion.getLocalDateOrNullFromObject;

import javax.transaction.Transactional;

import java.time.LocalDate;
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
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.measure.dto.MeasurableDTO;
import mil.af.abms.midas.api.measure.dto.MeasureDTO;
import mil.af.abms.midas.api.measure.dto.UpdateMeasureDTO;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.ProgressionStatus;

@Slf4j
@Service
public class MeasureService extends AbstractCRUDService<Measure, MeasureDTO, MeasureRepository> {

    private static final UnaryOperator<String> UPDATE_TOPIC = topic -> "/topic/update_" + topic.toLowerCase();

    private AssertionService assertionService;
    private CommentService commentService;
    private UserService userService;
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
    public void setUserService(UserService userService) { this.userService = userService; }

    @Transactional
    public Measure create(CreateMeasureDTO dto) {
        Measure newMeasure = Builder.build(Measure.class)
                .with(m -> m.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate())))
                .with(m -> m.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate())))
                .with(m -> m.setCompletedAt(null))
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

        foundMeasure.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate()));
        foundMeasure.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate()));
        foundMeasure.setCompletionType(dto.getCompletionType());
        foundMeasure.setText(dto.getText());

        calculateValueAndStatus(dto, foundMeasure, dto.getStatus());

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

    public void updateMeasureIfAssertionComplete(Measure measure, ProgressionStatus status, String assertionTitle) {
        if (ProgressionStatus.COMPLETED.equals(status)) {
            if (measure.getStatus() != ProgressionStatus.COMPLETED) {
                measure.setValue(measure.getTarget());
                measure.setStatus(ProgressionStatus.COMPLETED);
                measure.setCompletedAt(LocalDateTime.now());
                var userName = userService.getUserDisplayNameOrUsername();
                commentService.create(new CreateCommentDTO(
                        null,
                        null,
                        measure.getId(),
                        String.format("%s marked \"%s\" as completed, marking \"%s\" as complete!###COMPLETED", userName, assertionTitle, measure.getText())
                ), true);
                repository.save(measure);
            }
        }
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

    private void updateRelation(Assertion assertion, Measure measure) {
        Optional.ofNullable(assertion).map(a -> {
            a.getMeasures().add(measure);
            return a;
        }).ifPresent(a -> websocket.convertAndSend(UPDATE_TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

    private void calculateValueAndStatus(MeasurableDTO dto, Measure measure, ProgressionStatus dtoStatus) {
        var target = dto.getTarget();
        var value = dto.getValue();

        if (target == 0F) return;
        measure.setTarget(target);

        var comment = "";
        var userName = userService.getUserDisplayNameOrUsername();
        var isNewlyCompleted = ((value >= target || dtoStatus.equals(ProgressionStatus.COMPLETED)) && measure.getCompletedAt() == null);
        var isNotComplete = (value < target || !dtoStatus.equals(ProgressionStatus.COMPLETED));

        if (isNewlyCompleted) {
            measure.setStatus(ProgressionStatus.COMPLETED);
            measure.setValue(target);
            measure.setCompletedAt(LocalDateTime.now());

            if (value.equals(target)) {
                comment = String.format("%s set the value of \"%s\" equal to the given target, marking as complete.###COMPLETED", userName, measure.getText());
            } else if (dtoStatus.equals(ProgressionStatus.COMPLETED)) {
                comment = String.format("%s set the status of \"%s\" to complete, value has been set equal to the given target.###COMPLETED", userName, measure.getText());
            }
        } else if (isNotComplete) {
            ProgressionStatus newStatus;

            if (!dtoStatus.equals(ProgressionStatus.COMPLETED) && !dtoStatus.equals(measure.getStatus())) {
                newStatus = dtoStatus;
            } else if (measure.getDueDate() != null && measure.getDueDate().compareTo(LocalDate.now()) < 0) {
                newStatus = ProgressionStatus.BLOCKED;
                if (!measure.getStatus().equals(ProgressionStatus.BLOCKED)) {
                    comment = String.format("%s updated \"%s\" past the given due date without it being completed, marking as blocked.###BLOCKED", userName, measure.getText());
                }
            } else if (value.equals(0F)) {
                newStatus = ProgressionStatus.NOT_STARTED;
                if (!measure.getStatus().equals(ProgressionStatus.NOT_STARTED)) {
                    comment = String.format("%s set the value of \"%s\" to zero, marking as not started.###NOT_STARTED", userName, measure.getText());
                }
            } else {
                newStatus = ProgressionStatus.ON_TRACK;
                if (!measure.getStatus().equals(ProgressionStatus.ON_TRACK)) {
                    if (measure.getStartDate() == null) {
                        measure.setStartDate(LocalDate.now());
                        comment = String.format("%s updated the progress of \"%s\" within the given time range, marking as on track and setting start date to %s.###ON_TRACK", userName, measure.getText(), LocalDate.now());
                    } else {
                        comment = String.format("%s updated the progress of \"%s\" within the given time range, marking as on track.###ON_TRACK", userName, measure.getText());
                    }
                }
            }

            measure.setStatus(newStatus);
            measure.setValue(value);
            measure.setCompletedAt(null);
        }

        if (!comment.equals("")) {
            commentService.create(new CreateCommentDTO(
                    null,
                    null,
                    measure.getId(),
                    comment
            ), true);
        }
    }
}
