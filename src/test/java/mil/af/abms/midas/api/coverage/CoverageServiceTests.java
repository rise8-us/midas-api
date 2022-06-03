package mil.af.abms.midas.api.coverage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.enums.SonarqubeMaintainability;
import mil.af.abms.midas.enums.SonarqubeReliability;
import mil.af.abms.midas.enums.SonarqubeSecurity;

@ExtendWith(SpringExtension.class)
@Import(CoverageService.class)
class CoverageServiceTests {

    @MockBean
    SimpMessageSendingOperations websocket;
    @SpyBean
    CoverageService coverageService;
    @MockBean
    ProjectService projectService;
    @MockBean
    CoverageRepository coverageRepository;
    @Mock
    GitLab4JClient client;
    @Captor
    ArgumentCaptor<Coverage> coverageCaptor;
    @MockBean
    CustomProperty property;

    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private static final DecimalFormat df = new DecimalFormat("0.0");

    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(1L))
            .with(g -> g.setToken("foobarbaz"))
            .with(g -> g.setName("bar"))
            .with(g -> g.setDescription("foo"))
            .with(g -> g.setBaseUrl("http://foo.bar"))
            .with(g -> g.setCreationDate(CREATION_DATE))
            .get();
    private final Project project = Builder.build(Project.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setGitlabProjectId(3209))
            .with(p -> p.setSourceControl(sourceControl))
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
    void should_update_coverage_for_project() {

        doReturn(client).when(coverageService).getGitlabClient(any());
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
    void should_get_current() {
        when(coverageRepository.findCurrentForProject(any())).thenReturn(List.of(coverage));

        Coverage coverageExpected = coverageService.getCurrent(1L);

        assertThat(coverageExpected).isEqualTo(coverage);
    }

    @Test
    void should_set_coverage_from_project_id() {
        when(projectService.findById(1L)).thenReturn(project);
        doReturn(coverage).when(coverageService).updateCoverageForProject(project);
        var result = coverageService.updateCoverageForProjectById(1L);

        assertThat(result).isEqualTo(coverage);
    }

    @Test
    void should_skip_coverage_from_project_id() {
        Project projectWithoutGitLabConfig = new Project();
        BeanUtils.copyProperties(project, projectWithoutGitLabConfig);
        projectWithoutGitLabConfig.setSourceControl(null);

        when(projectService.findById(1L)).thenReturn(projectWithoutGitLabConfig);
        Coverage result = coverageService.updateCoverageForProjectById(1L);
        assertThat(result).isEqualTo(new Coverage());
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    void should_get_unknown_when_current_is_empty() {
        PageRequest pageRequest = PageRequest.of(1, 2);
        Page<Coverage> page = new PageImpl<Coverage>(List.of(), pageRequest, 0);
        when(coverageRepository.findAll(any(Specification.class), any(PageRequest.class))).thenReturn(page);

        Coverage coverageExpected = coverageService.getCurrent(1L);

        assertThat(coverageExpected).isEqualTo(new Coverage());
    }

    @Test
    void should_getGitlabClient() {
       assertThat(coverageService.getGitlabClient(project).getClass()).isEqualTo(GitLab4JClient.class);
    }

    @Test
    void should_throw_on_getGitlabClient() {
        var emptyProject = new Project();
        assertThrows(IllegalArgumentException.class, () -> coverageService.getGitlabClient(emptyProject));
    }

}
