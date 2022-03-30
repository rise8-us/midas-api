package mil.af.abms.midas.api.comment;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.Commentable;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.dto.CommentDTO;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.comment.dto.UpdateCommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.measure.MeasureService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.ProgressionStatus;

@Slf4j
@Service
public class CommentService extends AbstractCRUDService<Comment, CommentDTO, CommentRepository> {

    private static final UnaryOperator<String> TOPIC = clazzName -> "/topic/update_" + clazzName;

    private UserService userService;
    private AssertionService assertionService;
    private MeasureService measureService;
    private final SimpMessageSendingOperations websocket;

    public CommentService(CommentRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Comment.class, CommentDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setAssertionService(AssertionService assertionService) { this.assertionService = assertionService; }

    @Autowired
    public void setMeasureService(MeasureService measureService) { this.measureService = measureService; }

    @Transactional
    public Comment create(CreateCommentDTO dto, Boolean isSystemGenerated) {
        var newComment = Builder.build(Comment.class)
                .with(c -> c.setText(dto.getText()))
                .with(c -> c.setCreatedBy(Boolean.TRUE.equals(isSystemGenerated) ? userService.findByUsername("comment-system") : userService.getUserBySecContext()))
                .with(c -> c.setParent(findByIdOrNull(dto.getParentId())))
                .with(c -> c.setAssertion(assertionService.findByIdOrNull(dto.getAssertionId())))
                .with(c -> c.setMeasure(measureService.findByIdOrNull(dto.getMeasureId())))
                .get();

        var savedComment = repository.save(newComment);

        updateRelation(savedComment.getAssertion(), savedComment);
        updateRelation(savedComment.getMeasure(), savedComment);

        return savedComment;
    }

    public void createSystemComment(Long assertionId, Long measureId, String text) {
        var userName = userService.getUserDisplayNameOrUsername();
        create(new CreateCommentDTO(
                null,
                assertionId,
                measureId,
                String.format("%s - %s", userName, text)
        ), true);
    }

    @Transactional
    public Comment updateById(Long id, UpdateCommentDTO dto) {
        var comment = findById(id);
        comment.setText(dto.getText());
        comment.setLastEdit(LocalDateTime.now());
        comment.setEditedBy(userService.getUserBySecContext());

        return repository.save(comment);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        var comment = findById(id);
        var ogsm = Optional.ofNullable((Commentable) comment.getAssertion()).orElse(comment.getMeasure());
        comment.getChildren().forEach(c -> {
            ogsm.getComments().remove(c);
            repository.deleteById(c.getId());
        });
        ogsm.getComments().remove(comment);
        repository.deleteById(id);
        updateOGSMStatus(ogsm);
        websocket.convertAndSend(TOPIC.apply(ogsm.getLowercaseClassName()), ogsm.toDto());
    }

    @Transactional
    public void deleteAllRelatedComments(Comment comment) {
        comment.getChildren().forEach(this::deleteAllRelatedComments);
        removeRelationIfExists(comment.getAssertion(), comment);
        removeRelationIfExists(comment.getMeasure(), comment);
        repository.deleteById(comment.getId());
    }

    protected void removeRelationIfExists(Commentable commentable, Comment comment) {
        Optional.ofNullable(commentable).map(c -> {
            var comments = c.getComments().stream().filter(m -> !m.equals(comment)).collect(Collectors.toSet());
            c.setComments(comments);
            return c;
        }).ifPresent(a -> websocket.convertAndSend(TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

    protected void updateRelation(Commentable commentable, Comment comment) {
        Optional.ofNullable(commentable).map(a -> {
            a.getComments().add(comment);
            return a;
        }).ifPresent(a -> websocket.convertAndSend(TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

    private void updateOGSMStatus(Commentable commentable) {
        var statuses = getCommentIdsWithStatus(commentable);
        var newStatus = statuses.stream().max(Comparator.comparing(Pair::getFirst))
                .map(Pair::getSecond)
                .orElse(ProgressionStatus.NOT_STARTED);

        if (!commentable.getStatus().equals(newStatus)) {
            commentable.setStatus(newStatus);
        }
    }

    private List<Pair<Long, ProgressionStatus>> getCommentIdsWithStatus(Commentable commentable) {
        return commentable.getComments().stream()
                .filter(comment -> comment.getText().contains("###"))
                .map(c -> {
                    var status = ProgressionStatus.valueOf(c.getText().split("###")[1]);
                    return Pair.of(c.getId(), status);
                })
                .collect(Collectors.toList());
    }

}
