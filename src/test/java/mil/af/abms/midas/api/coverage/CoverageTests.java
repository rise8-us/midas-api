package mil.af.abms.midas.api.coverage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.gitlab4j.api.models.PipelineStatus;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.coverage.dto.CoverageDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.SonarQubeMaintainability;
import mil.af.abms.midas.enums.SonarQubeReliability;
import mil.af.abms.midas.enums.SonarQubeSecurity;

public class CoverageTests {

    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();
  
    private final Project project = Builder.build(Project.class)
            .with(p -> p.setId(3L)).get();
    private final Coverage coverage = Builder.build(Coverage.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setTestCoverage(98.6F))
            .with(c -> c.setCoverageChange(-1F))
            .with(c -> c.setMaintainabilityRating(SonarQubeMaintainability.A))
            .with(c -> c.setReliabilityRating(SonarQubeReliability.A))
            .with(c -> c.setSecurityRating(SonarQubeSecurity.A))
            .with(c -> c.setProject(project))
            .with(c -> c.setPipelineStatus(PipelineStatus.SUCCESS.toString()))
            .with(c -> c.setRef("ref"))
            .with(c -> c.setPipelineUrl("http://foo.bar"))
            .with(c -> c.setTriggeredBy("fizzBang"))
            .with(c -> c.setCreationDate(CREATION_DATE))
            .get();
    private final CoverageDTO coverageDTO = Builder.build(CoverageDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setJobId(-1))
            .with(d -> d.setTestCoverage(98.6F))
            .with(d -> d.setMaintainabilityRating(SonarQubeMaintainability.A))
            .with(d -> d.setReliabilityRating(SonarQubeReliability.A))
            .with(d -> d.setSecurityRating(SonarQubeSecurity.A))
            .with(d -> d.setProjectId(project.getId()))
            .with(d -> d.setCoverageChange(-1F))
            .with(c -> c.setPipelineStatus(PipelineStatus.SUCCESS.toString()))
            .with(d -> d.setRef("ref"))
            .with(d -> d.setPipelineUrl("http://foo.bar"))
            .with(d -> d.setTriggeredBy("fizzBang"))
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
        assertThat(coverage.getId()).isEqualTo(1L);
        assertThat(coverage.getTestCoverage()).isEqualTo(98.6F);
        assertThat(coverage.getMaintainabilityRating()).isEqualTo(SonarQubeMaintainability.A);
        assertThat(coverage.getReliabilityRating()).isEqualTo(SonarQubeReliability.A);
        assertThat(coverage.getSecurityRating()).isEqualTo(SonarQubeSecurity.A);
        assertThat(coverage.getProject()).isEqualTo(project);
        assertThat(coverage.getCreationDate()).isEqualTo(CREATION_DATE);
    }

    @Test
    public void should_return_dto() {
        assertThat(coverage.toDto()).isEqualTo(coverageDTO);
    }

    @Test
    public void should_be_equal() {
        Coverage coverage2 = new Coverage();
        BeanUtils.copyProperties(coverage, coverage2);

        assertEquals(coverage, coverage);
        assertNotEquals(coverage, null);
        assertNotEquals(coverage, new User());
        assertNotEquals(coverage, new Coverage());
        assertEquals(coverage, coverage2);
    }

}
