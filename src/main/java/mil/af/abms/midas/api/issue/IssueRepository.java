package mil.af.abms.midas.api.issue;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.issue.dto.IssueDTO;

public interface IssueRepository extends RepositoryInterface<Issue, IssueDTO> {
    Optional<Issue> findByIssueUid(String uId);

    @Query(value = "SELECT * FROM issue i WHERE i.project_id = :projectId", nativeQuery = true)
    Optional<List<Issue>> findAllIssuesByProjectId(Long projectId);
}
