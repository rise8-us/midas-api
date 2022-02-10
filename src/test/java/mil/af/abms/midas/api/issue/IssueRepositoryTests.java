package mil.af.abms.midas.api.issue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class IssueRepositoryTests extends RepositoryTestHarness {

    @Autowired
    IssueRepository issueRepository;

    @Test
    void should_find_by_issueUid() throws EntityNotFoundException {
        Project mock = Builder.build(Project.class)
                .with(p -> p.setName("name"))
                .with(p -> p.setGitlabProjectId(1))
                .get();

        entityManager.persist(mock);
        entityManager.flush();

        Issue issue = Builder.build(Issue.class)
                .with(i -> i.setIssueIid(1))
                .with(i -> i.setTitle("title"))
                .with(i -> i.setIssueUid("2-3-4"))
                .with(i -> i.setWeight(1L))
                .with(i -> i.setProject(mock))
                .get();

        Issue savedIssue = entityManager.persist(issue);
        entityManager.flush();

        Issue foundIssue = issueRepository.findByIssueUid(savedIssue.getIssueUid()).orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundIssue.getIssueUid()).isEqualTo("2-3-4");
    }

}
