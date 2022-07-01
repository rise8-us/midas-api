package mil.af.abms.midas.api.issue;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.exception.EntityNotFoundException;

class IssueRepositoryTests extends RepositoryTestHarness {

    @Autowired
    IssueRepository issueRepository;

    private Project savedProject;

    @BeforeEach
    void beforeEach() {
        Project project = Builder.build(Project.class)
            .with(p -> p.setName("name"))
            .with(p -> p.setGitlabProjectId(1))
            .get();

        savedProject = entityManager.persist(project);

        Issue issue = Builder.build(Issue.class)
                .with(i -> i.setIssueIid(1))
                .with(i -> i.setTitle("title"))
                .with(i -> i.setIssueUid("2-3-4"))
                .with(i -> i.setWeight(1L))
                .with(i -> i.setProject(savedProject))
                .with(i -> i.setCompletedAt(LocalDateTime.of(2000, 1, 1, 0, 0)))
                .get();

        entityManager.persist(issue);
        entityManager.flush();
    }

    @Test
    void should_find_by_issueUid() throws EntityNotFoundException {
        Issue foundIssue = issueRepository.findByIssueUid("2-3-4").orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundIssue.getIssueUid()).isEqualTo("2-3-4");
    }

    @Test
    void should_find_all_issues_by_projectId() throws EntityNotFoundException {
        List<Issue> foundIssues = issueRepository.findAllIssuesByProjectId(savedProject.getId()).orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundIssues).hasSize(1);
    }

    @Test
    void should_find_no_issues_by_projectId_and_date_range() throws EntityNotFoundException {
        List<Issue> noIssuesFound = issueRepository.findAllIssuesByProjectIdAndCompletedAtDateRange(
                savedProject.getId(),
                LocalDateTime.now(),
                LocalDateTime.now()
        ).orElseThrow(() -> new EntityNotFoundException("Not Found"));

        assertThat(noIssuesFound).hasSize(0);
    }

    @Test
    void should_find_all_issues_by_projectId_and_date_range() throws EntityNotFoundException {
        List<Issue> foundIssues = issueRepository.findAllIssuesByProjectIdAndCompletedAtDateRange(
                savedProject.getId(),
                LocalDateTime.of(1999, 1, 1, 1, 1),
                LocalDateTime.now()
        ).orElseThrow(() -> new EntityNotFoundException("Not Found"));

        assertThat(foundIssues).hasSize(1);
    }

}
