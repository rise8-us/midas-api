package mil.af.abms.midas.api.gitlabconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.coverage.Coverage;
import mil.af.abms.midas.api.coverage.dto.CoverageDTO;
import mil.af.abms.midas.api.gitlabconfig.dto.GitlabConfigDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;

public class GitlabConfigTests {

    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final GitlabConfig gitlabConfig = Builder.build(GitlabConfig.class)
            .with(g -> g.setId(1L))
            .with(g -> g.setToken("foobarbaz"))
            .with(g -> g.setName("bar"))
            .with(g -> g.setDescription("foo"))
            .with(g -> g.setBaseUrl("http://foo.bar"))
            .with(g -> g.setCreationDate(CREATION_DATE))
            .get();
    private final GitlabConfigDTO gitlabConfigDTO = Builder.build(GitlabConfigDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setName("bar"))
            .with(d -> d.setDescription("foo"))
            .with(d -> d.setBaseUrl("http://foo.bar"))
            .with(d -> d.setCreationDate(CREATION_DATE))
            .get();

    @Test
    public void should_have_all_coverage_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Coverage.class, fields::add);

        assertThat(fields.size()).isEqualTo(CoverageDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_set_and_get_properties() {
        assertThat(gitlabConfig.getId()).isEqualTo(1L);
        assertThat(gitlabConfig.getToken()).isEqualTo("foobarbaz");
        assertThat(gitlabConfig.getName()).isEqualTo("bar");
        assertThat(gitlabConfig.getBaseUrl()).isEqualTo("http://foo.bar");
        assertThat(gitlabConfig.getDescription()).isEqualTo("foo");
        assertThat(gitlabConfig.getCreationDate()).isEqualTo(CREATION_DATE);
    }

    @Test
    public void should_return_dto() {
        assertThat(gitlabConfig.toDto()).isEqualTo(gitlabConfigDTO);
    }

    @Test
    public void should_be_equal() {
        GitlabConfig gitlabConfig2 = new GitlabConfig();
        BeanUtils.copyProperties(gitlabConfig, gitlabConfig2);

        assertEquals(gitlabConfig, gitlabConfig);
        assertNotEquals(gitlabConfig, null);
        assertNotEquals(gitlabConfig, new User());
        assertNotEquals(gitlabConfig, new Coverage());
        assertEquals(gitlabConfig, gitlabConfig2);

        gitlabConfig2.setBaseUrl("http://fizz.bang");
        assertNotEquals(gitlabConfig, gitlabConfig2);
    }

}
