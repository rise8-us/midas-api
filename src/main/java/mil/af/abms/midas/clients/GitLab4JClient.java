package mil.af.abms.midas.clients;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Project;

import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.exception.GitApiException;

@Slf4j
@SuppressWarnings("unchecked")
public class GitLab4JClient {

    private static final String QUALITY_GATE_PATH = ".ci_artifacts/sonarqube/report_qualitygate_status.json";
    private static final String SONAR_LOG_PATH = ".ci_artifacts/sonarqube/sonar-scanner.log";
    private final GitLabApi client;

    public GitLab4JClient(String url, String token) {
        url = Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("A gitlab url must be provided"));
        token = Optional.ofNullable(token).orElseThrow(() -> new IllegalArgumentException("a gitlab token must be provided"));
        this.client = new GitLabApi(url, token);
    }

    public Optional<Project> findProjectById(Integer id) {
        return (Optional<Project>) makeRequest(() -> client.getProjectApi().getOptionalProject(id));
    }

    public Boolean projectExistsById(Integer id) {
        return findProjectById(id).isPresent();
    }

    public Map<String, String> getLatestCodeCoverage(Integer projectId, Integer currentJobId) {
        var coverage = Map.ofEntries(Map.entry("jobId", "-1"));
        var job = getLatestSonarQubeJob(projectId);
        Integer jobId = job.getId();

        if (jobId > currentJobId) {
            var artifact = Paths.get(QUALITY_GATE_PATH);
            var oStream = (Optional<InputStream>) makeRequestReturnOptional(
                    () -> client.getJobApi().downloadSingleArtifactsFile(projectId, jobId, artifact)
            );

            return oStream.map(s -> {
                var jobInfo = JsonMapper.getConditions(s);
                jobInfo.put("jobId", jobId.toString());
                jobInfo.put("pipelineUrl", job.getPipeline().getWebUrl());
                jobInfo.put("pipelineStatus", job.getPipeline().getStatus().toString());
                jobInfo.put("triggeredBy", job.getUser().getUsername());
                jobInfo.put("ref", job.getRef());
                jobInfo.put("sonarqubeUrl", getSonarqubeProjectUrl(projectId, jobId));
                return jobInfo;
            }).orElse(coverage);
        }
        return coverage;
    }

    public Job getLatestSonarQubeJob(Integer projectId) {
        List<Job> jobs = (List<Job>) makeRequest(() -> client.getJobApi().getJobs(projectId, 1, 200));
        return jobs.stream()
                .filter(j -> j.getName().equals("sonarqube"))
                .findFirst()
                .orElseThrow(() -> new GitApiException("Job", "sonarqube"));
    }

    public String getSonarqubeProjectUrl(Integer projectId, Integer jobId) {
        var artifact = Paths.get(SONAR_LOG_PATH);
        var oStream = (Optional<InputStream>) makeRequestReturnOptional(
                () -> client.getJobApi().downloadSingleArtifactsFile(projectId, jobId, artifact)
        );
        return oStream.map(s ->
                new BufferedReader(new InputStreamReader(s, StandardCharsets.UTF_8))
                    .lines()
                    .filter(l -> l.contains("you can browse"))
                    .map(l -> l.replaceAll("^.*https", "https"))
                    .collect(Collectors.joining("\n")))
                .orElse("url unknown");
    }

    @FunctionalInterface
    protected interface GitLabApiThunk<T> {
        T call() throws GitLabApiException, GitApiException;
    }

    @FunctionalInterface
    protected interface GitLabApiThunkOptional<T> {
        T call();
    }

    protected Object makeRequest(GitLabApiThunk<?> request) {
        try {
            return request.call();
        } catch (GitLabApiException e) {
            log.error(e.getMessage());
            throw new GitApiException(e.getLocalizedMessage());
        }
    }

    protected Object makeRequestReturnOptional(GitLabApiThunk<?> request) {
        try {
            return Optional.of(request.call());
        } catch (GitLabApiException e) {
            return Optional.empty();
        }
    }

}
