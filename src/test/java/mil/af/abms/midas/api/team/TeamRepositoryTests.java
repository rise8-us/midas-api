package mil.af.abms.midas.api.team;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.Builder;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class TeamRepositoryTests {

    @Autowired
    TestEntityManager entityManager;

    @Autowired
    TeamRepository teamRepository;

    @Test
    public void should_Find_By_Name() {

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
