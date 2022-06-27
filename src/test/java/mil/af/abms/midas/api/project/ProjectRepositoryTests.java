package mil.af.abms.midas.api.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.exception.EntityNotFoundException;

class ProjectRepositoryTests extends RepositoryTestHarness {

    @Autowired
    ProjectRepository projectRepository;

    private Team savedTeam;
    private Project testProject;

    @BeforeEach
    void beforeEach() {
        Team team = Builder.build(Team.class)
                .with(t -> t.setName("team"))
                .with(t -> t.setDescription("for testing"))
                .get();

        savedTeam = entityManager.persist(team);

        testProject = Builder.build(Project.class)
                .with(p -> p.setGitlabProjectId(1))
                .with(p -> p.setTeam(savedTeam))
                .with(p -> p.setName("foo"))
                .get();

        entityManager.persist(testProject);
        entityManager.flush();
    }

    @Test
    void should_throw_error_if_private_constructor_is_called() {
        Class<?> clazz = ProjectSpecifications.class;
        Constructor<?> constructor = clazz.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        Exception exception = assertThrows(InvocationTargetException.class, constructor::newInstance);
        assertThat(exception.getCause().getClass()).isEqualTo(IllegalStateException.class);
    }

    @Test
    void should_find_by_name() throws EntityNotFoundException {
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

    @Test
    void should_find_by_gitlab_project_id() throws EntityNotFoundException {
        Project foundProject = projectRepository.findByGitlabProjectId(testProject.getGitlabProjectId()).orElseThrow(() ->
            new EntityNotFoundException("Not Found"));

        assertThat(foundProject.getTeam()).isEqualTo(savedTeam);
        assertThat(foundProject).isEqualTo(testProject);
    }

    @Test
    void should_have_gitlab_id() throws EntityNotFoundException {
        List<Project> projects = projectRepository.findAll(ProjectSpecifications.hasGitlabProjectId());

        assertThat(projects.size()).isEqualTo(1);
    }
}
