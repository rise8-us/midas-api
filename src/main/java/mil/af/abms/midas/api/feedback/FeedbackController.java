package mil.af.abms.midas.api.feedback;


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
import mil.af.abms.midas.api.feedback.dto.CreateFeedbackDTO;
import mil.af.abms.midas.api.feedback.dto.FeedbackDTO;
import mil.af.abms.midas.api.feedback.dto.UpdateFeedbackDTO;
import mil.af.abms.midas.config.security.annotations.HasFeedbackEditAccess;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController extends AbstractCRUDController<Feedback, FeedbackDTO, FeedbackService> {

    @Autowired
    public FeedbackController(FeedbackService service) { super(service); }

    @PostMapping
    public FeedbackDTO create(@Valid @RequestBody CreateFeedbackDTO createFeedbackDTO) {
        return service.create(createFeedbackDTO).toDto();
    }

    @HasFeedbackEditAccess
    @PutMapping("/{id}")
    public FeedbackDTO updateById(@Valid @RequestBody UpdateFeedbackDTO updateFeedbackDTO, @PathVariable Long id) {
        return service.updateById(id, updateFeedbackDTO).toDto();
    }

    @Override
    @HasFeedbackEditAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
