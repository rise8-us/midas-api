package mil.af.abms.midas.api.coverage;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.coverage.dto.CoverageDTO;
import mil.af.abms.midas.api.gitlabconfig.GitlabConfig;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.clients.GitLab4JClient;
import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.enums.SonarqubeMaintainability;
import mil.af.abms.midas.enums.SonarqubeReliability;
import mil.af.abms.midas.enums.SonarqubeSecurity;

@Service
public class CoverageService extends AbstractCRUDService<Coverage, CoverageDTO, CoverageRepository> {

    private static final String COVERAGE = "coverage";
    private static final String RELIABILITY_RATING = "reliability_rating";
    private static final String SQALE_RATING = "sqale_rating";
    private static final String SECURITY_RATING = "security_rating";
    private static final String JOB_ID = "jobId";

    private final CustomProperty property;
    private ProjectService projectService;


    @Autowired
    public CoverageService(CoverageRepository repository, CustomProperty property) {
        super(repository, Coverage.class, CoverageDTO.class);
        this.property = property;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) { this.projectService =  projectService; }

    public Coverage updateCoverageForProject(Project project) {
        var client = getGitlabClient(project);
        var coverageCurrent = getCurrent(project.getId());
        Map<String, String> conditions = client.getLatestCodeCoverage(project.getGitlabProjectId(), coverageCurrent.getJobId());
        if (conditions.get(JOB_ID).equals("-1")) { return addJobInfoToCoverage(project, coverageCurrent); }
        var coverageNew = mapToCoverage(conditions, project, coverageCurrent.getTestCoverage());

        return repository.save(coverageNew);
    }

    public Coverage updateCoverageForProjectById(Long projectId) {
        if (property.getGitLabUrl().equals("NONE")) { return new Coverage(); }
        var project =  projectService.getObject(projectId);
        return updateCoverageForProject(project);
    }

    public Coverage getCurrent(Long projectId) {
       return repository.findCurrentForProject(projectId).stream().findFirst().orElseGet(Coverage::new);
    }

    private Coverage mapToCoverage(Map<String, String> conditions, Project project, Float currentCoverage) {

        var testCoverage = Float.parseFloat(conditions.get(COVERAGE));
        var maintainability = SonarqubeMaintainability.values()[Integer.parseInt(conditions.get(SQALE_RATING))];
        var reliability = SonarqubeReliability.values()[Integer.parseInt(conditions.get(RELIABILITY_RATING))];
        var security = SonarqubeSecurity.values()[Integer.parseInt(conditions.get(SECURITY_RATING))];

        return Builder.build(Coverage.class)
                .with(c -> c.setJobId(Integer.parseInt(conditions.get(JOB_ID))))
                .with(c -> c.setTestCoverage(testCoverage))
                .with(c -> c.setMaintainabilityRating(maintainability))
                .with(c -> c.setReliabilityRating(reliability))
                .with(c -> c.setSecurityRating(security))
                .with(c -> c.setProject(project))
                .with(c -> c.setCoverageChange(testCoverage - currentCoverage))
                .with(c -> c.setTriggeredBy(conditions.get("triggeredBy")))
                .with(c -> c.setPipelineUrl(conditions.get("pipelineUrl")))
                .with(c -> c.setPipelineStatus(conditions.get("pipelineStatus")))
                .with(c -> c.setRef(conditions.get("ref")))
                .with(c -> c.setSonarqubeUrl(conditions.get("sonarqubeUrl")))
                .get();
    }

    private Coverage addJobInfoToCoverage(Project project, Coverage coverage) {
        var client = getGitlabClient(project);
        Map<String, String> jobInfo = client.getJobInfo(project.getGitlabProjectId(), coverage.getJobId());
        coverage.setRef(jobInfo.get("ref"));
        coverage.setPipelineUrl(jobInfo.get("pipelineUrl"));
        coverage.setPipelineStatus(jobInfo.get("pipelineStatus"));
        coverage.setTriggeredBy(jobInfo.get("triggeredBy"));
        return coverage;
    }

    protected GitLab4JClient getGitlabClient(Project project) {
        var url = Optional.ofNullable(project.getGitlabConfig()).map(GitlabConfig::getBaseUrl).orElse(null);
        var token = Optional.ofNullable(project.getGitlabConfig()).map(GitlabConfig::getToken).orElse(null);
        return new GitLab4JClient(url, token);
    }

}
