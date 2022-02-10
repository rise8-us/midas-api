package mil.af.abms.midas.api.issue;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.issue.dto.IssueDTO;

public interface IssueRepository extends RepositoryInterface<Issue, IssueDTO> {
    Optional<Issue> findByIssueUid(String uId);
}
