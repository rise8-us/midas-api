package mil.af.abms.midas.api.feedback;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.feedback.dto.CreateFeedbackDTO;
import mil.af.abms.midas.api.feedback.dto.FeedbackDTO;
import mil.af.abms.midas.api.feedback.dto.UpdateFeedbackDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.UserService;

@Service
public class FeedbackService extends AbstractCRUDService<Feedback, FeedbackDTO, FeedbackRepository> {

    private UserService userService;

    public FeedbackService(FeedbackRepository repository) {
        super(repository, Feedback.class, FeedbackDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Transactional
    public Feedback create(CreateFeedbackDTO dto) {
        var newFeedback = Builder.build(Feedback.class)
                .with(f -> f.setCreatedBy(userService.getUserBySecContext()))
                .with(f -> f.setRating(dto.getRating()))
                .with(f -> f.setRelatedTo(dto.getRelatedTo()))
                .with(f -> f.setNotes(dto.getNotes()))
                .get();

        return repository.save(newFeedback);
    }

    @Transactional
    public Feedback updateById(Long id, UpdateFeedbackDTO dto) {
        var foundFeedback = findById(id);

        foundFeedback.setEditedBy(userService.getUserBySecContext());
        foundFeedback.setEditedAt(LocalDateTime.now());
        foundFeedback.setRating(dto.getRating());
        foundFeedback.setNotes(dto.getNotes());

        return repository.save(foundFeedback);
    }

}
