package mil.af.abms.midas.api.comment;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.comment.dto.CommentDTO;
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
import mil.af.abms.midas.api.comment.dto.UpdateCommentDTO;
import mil.af.abms.midas.config.security.annotations.HasCommentDelete;

@RestController
@RequestMapping("/api/comments")
public class CommentController extends AbstractCRUDController<Comment, CommentDTO, CommentService> {

    @Autowired
    public CommentController(CommentService service) {
        super(service);
    }

    @PostMapping
    public CommentDTO create(@Valid @RequestBody CreateCommentDTO createCommentDTO) {
        return service.create(createCommentDTO, false).toDto();
    }

    @PutMapping("/{id}")
    public CommentDTO updateById(@Valid @RequestBody UpdateCommentDTO updateCommentDTO, @PathVariable Long id) {
        return service.updateById(id, updateCommentDTO).toDto();
    }

    @Override
    @HasCommentDelete
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
