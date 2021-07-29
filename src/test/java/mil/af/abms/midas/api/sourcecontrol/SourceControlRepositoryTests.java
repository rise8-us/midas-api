package mil.af.abms.midas.api.sourcecontrol;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.RepositoryTestHarness;
import mil.af.abms.midas.api.helper.AttributeEncryptor;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.exception.EntityNotFoundException;


@Import({ AttributeEncryptor.class })
class SourceControlRepositoryTests extends RepositoryTestHarness {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private SourceControlRepository sourceControlRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final SourceControl config = Builder.build(SourceControl.class)
            .with(c -> c.setName("foo"))
            .with(c -> c.setDescription("bar"))
            .with(c -> c.setBaseUrl("http://fizz.bang"))
            .with(c -> c.setToken("foobarbaz"))
            .get();

    @BeforeEach void init() {
        entityManager.persist(config);
        entityManager.flush();
    }

    @Test
    void should_find_by_name() throws EntityNotFoundException {
        SourceControl foundSourceControl = sourceControlRepository.findByName(config.getName()).orElseThrow(() ->
            new EntityNotFoundException("Not Found"));

        assertThat(foundSourceControl.getToken()).isEqualTo(config.getToken());
    }

    @Test
    void readEncrypted() {
        SourceControl configReturned = jdbcTemplate.queryForObject(
                "select * from source_control where name = ?",
                (resultSet, i) -> {
                    SourceControl result = new SourceControl();
                    result.setName(resultSet.getString("name"));
                    result.setToken(resultSet.getString("token"));
                    return result;
                },
                "foo"
        );

        assert configReturned != null;
        assertThat(configReturned.getToken()).isNotEqualTo(config.getToken());
        assertThat(encryptor.convertToEntityAttribute(configReturned.getToken())).isEqualTo(config.getToken());
    }
}
