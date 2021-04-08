package mil.af.abms.midas.api.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class ProjectRepositoryTests {

    @Autowired
    TestEntityManager entityManager;
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
                .with(p -> p.setGitlabProjectId(1L))
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
