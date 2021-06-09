package mil.af.abms.midas.api.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.exception.EntityNotFoundException;

public class ProjectRepositoryTests extends RepositoryTestHarness {

    @Autowired
    ProjectRepository projectRepository;

    @Test
    public void should_find_by_name() throws EntityNotFoundException {

        Team team = Builder.build(Team.class)
            .with(t -> t.setName("team"))
            .with(t -> t.setGitlabGroupId(1L))
            .with(t -> t.setDescription("for testing")).get();

        Team savedTeam = entityManager.persist(team);

        Project testProject = Builder.build(Project.class)
                .with(p -> p.setGitlabProjectId(1))
                .with(p -> p.setTeam(savedTeam))
                .with(p -> p.setName("foo")).get();
                
        entityManager.persist(testProject);
        entityManager.flush();

        Project foundProject = projectRepository.findByName(testProject.getName()).orElseThrow(() ->
            new EntityNotFoundException("Not Found"));

        assertThat(foundProject.getTeam()).isEqualTo(savedTeam);
        assertThat(foundProject).isEqualTo(testProject);

        foundProject.setTeam(null);

        entityManager.persist(foundProject);
        entityManager.flush();

        Project projectNoTeam = projectRepository.findById(foundProject.getId()).orElseThrow(() ->
            new EntityNotFoundException("Not found"));

        assertThat(projectNoTeam.getTeam()).isEqualTo(null);
    }
}
