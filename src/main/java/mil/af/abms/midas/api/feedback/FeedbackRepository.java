package mil.af.abms.midas.api.feedback;

import org.springframework.stereotype.Repository;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.feedback.dto.FeedbackDTO;

@Repository
public interface FeedbackRepository extends RepositoryInterface<Feedback, FeedbackDTO> {
}
