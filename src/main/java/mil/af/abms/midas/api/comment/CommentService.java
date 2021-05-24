package mil.af.abms.midas.api.comment;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.dto.CommentDTO;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.comment.dto.UpdateCommentDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.UserService;

@Service
public class CommentService extends AbstractCRUDService<Comment, CommentDTO, CommentRepository> {

    UserService userService;
    AssertionService assertionService;

    public CommentService(CommentRepository repository) { super(repository, Comment.class, CommentDTO.class); }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setAssertionService(AssertionService assertionService) { this.assertionService = assertionService; }

    @Transactional
    public Comment create(CreateCommentDTO createCommentDTO) {
        Comment newComment = Builder.build(Comment.class)
                .with(c -> c.setText(createCommentDTO.getText()))
                .with(c -> c.setCreatedBy(userService.getUserBySecContext()))
                .with(c -> c.setParent(findByIdOrNull(createCommentDTO.getParentId())))
                .with(c -> c.setAssertion(assertionService.findByIdOrNull(createCommentDTO.getAssertionId()))).get();

        return repository.save(newComment);
    }

    @Transactional
    public Comment updateById(Long id, UpdateCommentDTO updateCommentDTO) {
        Comment comment = getObject(id);
        comment.setText(updateCommentDTO.getText());
        comment.setLastEdit(LocalDateTime.now());

        return repository.save(comment);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        getObject(id).getChildren().forEach(c -> deleteById(c.getId()));
        repository.deleteById(id);
    }
 }
