package mil.af.abms.midas.api.project;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.coverage.Coverage;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.project.dto.ProjectDTO;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.SyncStatus;

public class ProjectTests {

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final Set<Tag> tags = Set.of(Builder.build(Tag.class).with(u -> u.setId(2L)).get());
    private final Team team = Builder.build(Team.class)
            .with(t -> t.setId(3L)).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(3L)).get();
    private final Coverage coverageOld = Builder.build(Coverage.class)
            .with(c -> c.setId(9L))
            .with(c -> c.setCreationDate(CREATION_DATE))
            .get();
    private final Coverage coverage = Builder.build(Coverage.class)
            .with(c -> c.setId(10L))
            .with(c -> c.setCreationDate(CREATION_DATE))
            .get();
    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(42L))
            .with(g -> g.setName("Mock IL2"))
            .get();

    Project expectedProject = Builder.build(Project.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("MIDAS"))
            .with(p -> p.setDescription("testDescription"))
            .with(p -> p.setTeam(team))
            .with(p -> p.setIsArchived(true))
            .with(p -> p.setGitlabProjectId(2))
            .with(p -> p.setTags(tags))
            .with(p -> p.setWebUrl("web.url"))
            .with(p -> p.setProjectJourneyMap(0L))
            .with(p -> p.setCoverages(Set.of(coverage, coverageOld)))
            .with(p -> p.setProduct(product))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setCreationDate(CREATION_DATE))
            .get();

    ProjectDTO expectedProjectDTO = Builder.build(ProjectDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setName("MIDAS"))
            .with(d -> d.setDescription("testDescription"))
            .with(d -> d.setIsArchived(true))
            .with(d -> d.setTeamId(3L))
            .with(d -> d.setGitlabProjectId(2))
            .with(d -> d.setProjectJourneyMap(0L))
            .with(d -> d.setTagIds(Set.of(2L)))
            .with(d -> d.setWebUrl("web.url"))
            .with(d -> d.setProductId(product.getId()))
            .with(d -> d.setCoverage(coverage.toDto()))
            .with(d -> d.setSourceControlId(sourceControl.getId()))
            .with(d -> d.setCreationDate(CREATION_DATE))
            .with(d -> d.setIssueSyncStatus(SyncStatus.SYNCED))
            .with(d -> d.setReleaseSyncStatus(SyncStatus.SYNCED))
            .with(d -> d.setLatestRelease(new ReleaseDTO()))
            .get();

    @Test
    public void should_have_all_projectDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Project.class, fields::add);

        assertThat(fields.size()).isEqualTo(ProjectDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_set_and_get_properties() {
        assertThat(expectedProject.getId()).isEqualTo(1L);
        assertThat(expectedProject.getName()).isEqualTo("MIDAS");
        assertThat(expectedProject.getTeam()).isEqualTo(team);
        assertThat(expectedProject.getDescription()).isEqualTo("testDescription");
        assertThat(expectedProject.getProjectJourneyMap()).isEqualTo(0L);
        assertTrue(expectedProject.getIsArchived());
        assertThat(expectedProject.getGitlabProjectId()).isEqualTo(2L);
        assertThat(expectedProject.getSourceControl()).isEqualTo(sourceControl);
        assertThat(expectedProject.getCreationDate()).isEqualTo(CREATION_DATE);
    }

    @Test
    public void should_return_dto() {
        assertThat(expectedProject.toDto()).isEqualTo(expectedProjectDTO);
    }

    @Test
    public void should_return_dto_null_team() {
        expectedProject.setTeam(null);

        assertThat(expectedProject.toDto().getTeamId()).isEqualTo(null);
    }

    @Test
    public void should_return_dto_null_product() {
        expectedProject.setProduct(null);

        assertThat(expectedProject.toDto().getProductId()).isEqualTo(null);
    }

    @Test public  void should_get_current_coverage() {
        assertThat(expectedProject.getCurrentCoverage()).isEqualTo(coverage);
    }

    @Test
    public void should_be_equal() {
        Project project2 = Builder.build(Project.class)
                .with(p -> p.setName("MIDAS")).get();

        assertEquals(expectedProject, project2);
        assertNotEquals(expectedProject, null);
        assertNotEquals(expectedProject, new User());
        assertNotEquals(expectedProject, new Project());
        assertEquals(expectedProject, project2);
    }
}
