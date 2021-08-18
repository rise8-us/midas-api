package mil.af.abms.midas.api.comment;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.dto.CommentDTO;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.comment.dto.UpdateCommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.UserService;

@Service
public class CommentService extends AbstractCRUDService<Comment, CommentDTO, CommentRepository> {

    private UserService userService;
    private AssertionService assertionService;

    public CommentService(CommentRepository repository) { super(repository, Comment.class, CommentDTO.class); }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setAssertionService(AssertionService assertionService) { this.assertionService = assertionService; }

    @Autowired SimpMessageSendingOperations websocket;

    @Transactional
    public Comment create(CreateCommentDTO dto) {
        var newComment = Builder.build(Comment.class)
                .with(c -> c.setText(dto.getText()))
                .with(c -> c.setCreatedBy(userService.getUserBySecContext()))
                .with(c -> c.setParent(findByIdOrNull(dto.getParentId())))
                .with(c -> c.setAssertion(assertionService.findByIdOrNull(dto.getAssertionId()))).get();

        repository.save(newComment);
        newComment.getAssertion().getComments().add(newComment);
        websocket.convertAndSend("/topic/update_assertion", newComment.getAssertion().toDto());
        return newComment;
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
        removeAssertionRelationIfExists(comment);
        comment.getChildren().forEach(c -> deleteById(c.getId()));
        repository.deleteById(id);
    }

    public void deleteComment(Comment comment) {
        comment.getChildren().forEach(this::deleteComment);
        repository.deleteById(comment.getId());
    }

    protected void removeAssertionRelationIfExists(Comment comment) {
        var assertion = Optional.ofNullable(comment.getAssertion()).orElse(new Assertion());
        assertion.getComments().remove(comment);
        websocket.convertAndSend("/topic/update_assertion", assertion.toDto());
    }
 }
