package mil.af.abms.midas.api.comment;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.UnaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
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
    public Comment create(CreateCommentDTO dto) {
        var newComment = Builder.build(Comment.class)
                .with(c -> c.setText(dto.getText()))
                .with(c -> c.setCreatedBy(userService.getUserBySecContext()))
                .with(c -> c.setParent(findByIdOrNull(dto.getParentId())))
                .with(c -> c.setAssertion(assertionService.findByIdOrNull(dto.getAssertionId())))
                .with(c -> c.setMeasure(measureService.findByIdOrNull(dto.getMeasureId())))
                .get();

        var savedComment = repository.save(newComment);

        updateRelation(savedComment.getAssertion(), savedComment);
        updateRelation(savedComment.getMeasure(), savedComment);

        return savedComment;
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
        removeRelationIfExists(comment.getAssertion(), comment);
        removeRelationIfExists(comment.getMeasure(), comment);
        comment.getChildren().forEach(c -> deleteById(c.getId()));
        repository.deleteById(id);
    }

    @Transactional
    public void deleteComment(Comment comment) {
        comment.getChildren().forEach(this::deleteComment);
        deleteById(comment.getId());
    }

    protected void removeRelationIfExists(Commentable commentable, Comment comment) {
        Optional.ofNullable(commentable).map(a -> {
            a.getComments().remove(comment);
            return a;
        }).ifPresent(a -> websocket.convertAndSend(TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

    protected void updateRelation(Commentable commentable, Comment comment) {
        Optional.ofNullable(commentable).map(a -> {
            a.getComments().add(comment);
            return a;
        }).ifPresent(a -> websocket.convertAndSend(TOPIC.apply(a.getLowercaseClassName()), a.toDto()));
    }

 }
