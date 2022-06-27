package mil.af.abms.midas.api.release;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.exception.EntityNotFoundException;

class ReleaseRepositoryTests extends RepositoryTestHarness {

    @Autowired
    ReleaseRepository releaseRepository;

    @Test
    void should_find_by_releaseUid() throws EntityNotFoundException {
        Project project = Builder.build(Project.class)
                .with(p -> p.setName("name"))
                .with(p -> p.setGitlabProjectId(1))
                .get();

        entityManager.persist(project);
        entityManager.flush();

        Release release = Builder.build(Release.class)
                .with(i -> i.setName("title"))
                .with(i -> i.setUid("2-3-4"))
                .with(i -> i.setProject(project))
                .get();

        Release savedRelease = entityManager.persist(release);
        entityManager.flush();

        Release foundRelease = releaseRepository.findByUid(savedRelease.getUid()).orElseThrow(() ->
                new EntityNotFoundException("Not Found"));

        assertThat(foundRelease.getUid()).isEqualTo("2-3-4");
    }

}
