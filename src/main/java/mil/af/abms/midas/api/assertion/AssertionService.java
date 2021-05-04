package mil.af.abms.midas.api.assertion;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.ogsm.OgsmService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.AssertionStatus;

@Service
public class AssertionService extends AbstractCRUDService<Assertion, AssertionDTO, AssertionRepository> {

    private UserService userService;
    private OgsmService ogsmService;
    private CommentService commentService;

    public AssertionService(AssertionRepository repository) {
        super(repository, Assertion.class, AssertionDTO.class);
    }

    @Autowired
    public void setOgsmService(OgsmService ogsmService) { this.ogsmService = ogsmService; }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Autowired
    public void setCommentService(CommentService commentService) { this.commentService = commentService; }

    @Transactional
    public Assertion create(CreateAssertionDTO createAssertionDTO) {

        Assertion newAssertion = Builder.build(Assertion.class)
                .with(a -> a.setOgsm(ogsmService.getObject(createAssertionDTO.getOgsmId())))
                .with(a -> a.setText(createAssertionDTO.getText()))
                .with(a -> a.setType(createAssertionDTO.getType()))
                .with(a -> a.setStatus(AssertionStatus.NOT_STARTED))
                .with(a -> a.setCreatedBy(userService.getUserBySecContext())).get();

        return repository.save(newAssertion);
    }

    @Transactional
    public Assertion updateById(Long id, UpdateAssertionDTO updateAssertionDTO) {
        Assertion assertion = getObject(id);
        Set<Comment> comments = updateAssertionDTO.getCommentIds()
                .stream().map(commentService::getObject).collect(Collectors.toSet());

        assertion.setText(updateAssertionDTO.getText());
        assertion.setStatus(AssertionStatus.NOT_STARTED);
        assertion.setType(updateAssertionDTO.getType());
        assertion.setComments(comments);
  
        return repository.save(assertion);
    }

}
