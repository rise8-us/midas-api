package mil.af.abms.midas.api.coverage;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
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
import mil.af.abms.midas.enums.SonarqubeMaintainability;
import mil.af.abms.midas.enums.SonarqubeReliability;
import mil.af.abms.midas.enums.SonarqubeSecurity;

class CoverageTests {

    private final Project project = Builder.build(Project.class)
            .with(p -> p.setId(3L)).get();
    private final Coverage coverage = Builder.build(Coverage.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setTestCoverage(98.6F))
            .with(c -> c.setCoverageChange(-1F))
            .with(c -> c.setMaintainabilityRating(SonarqubeMaintainability.A))
            .with(c -> c.setReliabilityRating(SonarqubeReliability.A))
            .with(c -> c.setSecurityRating(SonarqubeSecurity.A))
            .with(c -> c.setProject(project))
            .with(c -> c.setPipelineStatus(PipelineStatus.SUCCESS.toString()))
            .with(c -> c.setRef("ref"))
            .with(c -> c.setPipelineUrl("http://foo.bar"))
            .with(c -> c.setTriggeredBy("fizzBang"))
            .get();
    private final CoverageDTO coverageDTO = Builder.build(CoverageDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setJobId(-1))
            .with(d -> d.setTestCoverage(98.6F))
            .with(d -> d.setMaintainabilityRating(SonarqubeMaintainability.A))
            .with(d -> d.setReliabilityRating(SonarqubeReliability.A))
            .with(d -> d.setSecurityRating(SonarqubeSecurity.A))
            .with(d -> d.setProjectId(project.getId()))
            .with(d -> d.setCoverageChange(-1F))
            .with(c -> c.setPipelineStatus(PipelineStatus.SUCCESS.toString()))
            .with(d -> d.setRef("ref"))
            .with(d -> d.setPipelineUrl("http://foo.bar"))
            .with(d -> d.setTriggeredBy("fizzBang"))
            .with(d -> d.setCreationDate(coverage.getCreationDate()))
            .get();

    @Test
    void should_have_all_coverage_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Coverage.class, fields::add);

        assertThat(fields).hasSize(CoverageDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_set_and_get_properties() {
        assertThat(coverage.getId()).isEqualTo(1L);
        assertThat(coverage.getTestCoverage()).isEqualTo(98.6F);
        assertThat(coverage.getMaintainabilityRating()).isEqualTo(SonarqubeMaintainability.A);
        assertThat(coverage.getReliabilityRating()).isEqualTo(SonarqubeReliability.A);
        assertThat(coverage.getSecurityRating()).isEqualTo(SonarqubeSecurity.A);
        assertThat(coverage.getProject()).isEqualTo(project);
    }

    @Test
    void should_return_dto() {
        assertThat(coverage.toDto()).isEqualTo(coverageDTO);
    }

    @Test
    void should_be_equal() {
        Coverage coverage2 = new Coverage();
        BeanUtils.copyProperties(coverage, coverage2);

        assertThat(coverage).isEqualTo(coverage);
        assertThat(coverage).isNotNull();
        assertThat(coverage).isNotEqualTo(new User());
        assertThat(coverage).isNotSameAs(new Coverage());
        assertThat(coverage).isEqualTo(coverage2);
    }

}
