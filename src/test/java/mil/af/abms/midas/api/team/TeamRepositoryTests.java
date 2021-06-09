package mil.af.abms.midas.api.team;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.Builder;

public class TeamRepositoryTests extends RepositoryTestHarness {

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void should_find_by_name() {

        Team testTeam = Builder.build(Team.class)
                .with(t -> t.setGitlabGroupId(1L))
                .with(t -> t.setDescription("dev team"))
                .with(t -> t.setName("foo")).get();

        entityManager.persist(testTeam);
        entityManager.flush();

        Optional<Team> foundTeam = teamRepository.findByName(testTeam.getName());

        assertThat(foundTeam.orElse(new Team())).isEqualTo(testTeam);
    }
}
