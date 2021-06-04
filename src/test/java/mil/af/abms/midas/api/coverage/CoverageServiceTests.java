package mil.af.abms.midas.api.coverage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.clients.GitLab4JClient;
import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.enums.SonarqubeMaintainability;
import mil.af.abms.midas.enums.SonarqubeReliability;
import mil.af.abms.midas.enums.SonarqubeSecurity;

@ExtendWith(SpringExtension.class)
@Import(CoverageService.class)
public class CoverageServiceTests {

    @SpyBean
    CoverageService coverageService;
    @MockBean
    ProjectService projectService;
    @MockBean
    CoverageRepository coverageRepository;
    @MockBean
    GitLab4JClient client;
    @Captor
    ArgumentCaptor<Coverage> coverageCaptor;
    @MockBean
    CustomProperty property;

    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private static final DecimalFormat df = new DecimalFormat("0.0");

    Project project = Builder.build(Project.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setGitlabProjectId(3209))
            .get();
    private final Coverage coverage = Builder.build(Coverage.class)
            .with(c -> c.setId(2L))
            .with(c -> c.setTestCoverage(98.6F))
            .with(c -> c.setMaintainabilityRating(SonarqubeMaintainability.A))
            .with(c -> c.setReliabilityRating(SonarqubeReliability.A))
            .with(c -> c.setSecurityRating(SonarqubeSecurity.A))
            .with(c -> c.setProject(project))
            .with(c -> c.setCreationDate(CREATION_DATE))
            .get();
    private final Coverage coveragePrevious = Builder.build(Coverage.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setTestCoverage(100F))
            .get();
    Map<String, String> conditions = Map.ofEntries(
            Map.entry("jobId", "1"),
            Map.entry("coverage", "98.6"),
            Map.entry("reliability_rating", "1"),
            Map.entry("sqale_rating", "1"),
            Map.entry("security_rating", "1"),
            Map.entry("ref", "master"),
            Map.entry("pipelineUrl", "http://foo.bar"),
            Map.entry("pipelineStatus", "SUCCESS"),
            Map.entry("triggeredBy", "fizzBang")
    );

    
    @Test
    public void should_update_coverage_for_project() {

        doReturn(coveragePrevious).when(coverageService).getCurrent(1L);
        when(coverageRepository.save(any())).thenReturn(coverage);
        when(client.getLatestCodeCoverage(any(), any())).thenReturn(conditions);

        coverageService.updateCoverageForProject(project);

        verify(coverageRepository).save(coverageCaptor.capture());
        Coverage capturedCoverage = coverageCaptor.getValue();

        assertThat(capturedCoverage.getTestCoverage()).isEqualTo(98.6F);
        assertThat(capturedCoverage.getSecurityRating()).isEqualTo(SonarqubeSecurity.A);
        assertThat(capturedCoverage.getReliabilityRating()).isEqualTo(SonarqubeReliability.A);
        assertThat(capturedCoverage.getMaintainabilityRating()).isEqualTo(SonarqubeMaintainability.A);
        assertThat(capturedCoverage.getRef()).isEqualTo("master");
        assertThat(capturedCoverage.getPipelineStatus()).isEqualTo("SUCCESS");
        assertThat(capturedCoverage.getPipelineUrl()).isEqualTo("http://foo.bar");
        assertThat(capturedCoverage.getTriggeredBy()).isEqualTo("fizzBang");
        assertThat(df.format(capturedCoverage.getCoverageChange())).isEqualTo(df.format(-1.4F));

        assertThat(capturedCoverage.getProject()).isEqualTo(project);

    }

    @Test
    public void should_skip_update_coverage_for_project() {
        Map<String, String> emptyConditions = Map.ofEntries(Map.entry("jobId", "-1"));
        doReturn(coveragePrevious).when(coverageService).getCurrent(1L);
        when(client.getLatestCodeCoverage(any(), any())).thenReturn(emptyConditions);
        when(client.getJobInfo(any(), any())).thenReturn(conditions);

        Coverage coverageReturned = coverageService.updateCoverageForProject(project);

        assertThat(coverageReturned.getTriggeredBy()).isEqualTo("fizzBang");
        assertThat(coverageReturned.getPipelineUrl()).isEqualTo("http://foo.bar");
        assertThat(coverageReturned.getRef()).isEqualTo("master");
        assertThat(coverageReturned.getPipelineStatus()).isEqualTo("SUCCESS");
    }


        @Test
    public void should_get_current() {
        when(coverageRepository.findCurrentForProject(any())).thenReturn(List.of(coverage));

        Coverage coverageExpected = coverageService.getCurrent(1L);

        assertThat(coverageExpected).isEqualTo(coverage);
    }

    @Test public void should_set_coverage_from_project_id() {
        when(property.getGitLabUrl()).thenReturn("http://foo.bar");
        when(projectService.getObject(1L)).thenReturn(project);
        doReturn(coverage).when(coverageService).updateCoverageForProject(project);
        coverageService.updateCoverageForProjectById(1L);
    }

    @Test public void should_skip_coverage_from_project_id() {
        when(property.getGitLabUrl()).thenReturn("NONE");
        Coverage result = coverageService.updateCoverageForProjectById(1L);
        assertThat(result).isEqualTo(new Coverage());
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    public void should_get_unknown_when_current_is_empty() {
        PageRequest pageRequest = PageRequest.of(1, 2);
        Page<Coverage> page = new PageImpl<Coverage>(List.of(), pageRequest, 0);
        when(coverageRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Coverage coverageExpected = coverageService.getCurrent(1L);

        assertThat(coverageExpected).isEqualTo(new Coverage());
    }

}
