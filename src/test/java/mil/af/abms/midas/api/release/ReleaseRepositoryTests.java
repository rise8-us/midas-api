package mil.af.abms.midas.api.release;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.exception.EntityNotFoundException;

class ReleaseRepositoryTests extends RepositoryTestHarness {

    @Autowired
    ReleaseRepository releaseRepository;

    private Project savedProject;

    @BeforeEach()
    void beforeEach() {
        Project project = Builder.build(Project.class)
                .with(p -> p.setName("name"))
                .with(p -> p.setGitlabProjectId(1))
                .get();

        savedProject = entityManager.persist(project);

        Release release = Builder.build(Release.class)
                .with(i -> i.setName("title"))
                .with(i -> i.setUid("2-3-4"))
                .with(i -> i.setProject(savedProject))
                .with(i -> i.setReleasedAt(LocalDateTime.of(2000, 1, 1, 1, 1)))
                .get();

        entityManager.persist(release);
        entityManager.flush();
    }

    @Test
    void should_find_by_releaseUid() throws EntityNotFoundException {
        Release foundRelease = releaseRepository.findByUid("2-3-4").orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundRelease.getUid()).isEqualTo("2-3-4");
    }

    @Test
    void should_find_previous_release_by_project_id_and_date() throws EntityNotFoundException {
        Release foundRelease = releaseRepository.findPreviousReleaseByProjectIdAndReleasedAt(
                savedProject.getId(),
                LocalDateTime.of(2001, 1, 1, 1, 1)
        ).orElseThrow(() -> new EntityNotFoundException("Not Found"));

        assertThat(foundRelease.getUid()).isEqualTo("2-3-4");
    }

}
