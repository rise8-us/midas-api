package mil.af.abms.midas.clients;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;

import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.exception.GitApiException;

@Slf4j
@SuppressWarnings("unchecked")
@Service
public class GitLab4JClient {
    private static final String QUALITY_GATE_PATH = ".ci_artifacts/sonarqube/report_qualitygate_status.json";
    private final GitLabApi client;

    public GitLab4JClient(@Autowired CustomProperty property) {
        this.client = new GitLabApi(property.getGitLabUrl(), property.getGitLabAccessToken());
    }

    public Optional<Project> findProjectById(Integer id) {
        return (Optional<Project>) makeRequest(() -> client.getProjectApi().getOptionalProject(id));
    }

    public Boolean projectExistsById(Integer id) {
        return findProjectById(id).isPresent();
    }

    public Project getProjectById(Integer id) {
        return findProjectById(id).orElseThrow(() -> new GitApiException(Project.class.getSimpleName(), id));
    }

    public Optional<User> findUserByUsername(String username) {
        return (Optional<User>) makeRequest(() -> client.getUserApi().getOptionalUser(username));
    }

    public User getUserByUsername(String username) {
        return findUserByUsername(username).orElseThrow(() -> new GitApiException(User.class.getSimpleName(), username));
    }

    public Map<String, String> getLatestCodeCoverage(Integer projectId, Integer currentJobId) {
        Map<String, String> coverage = Map.ofEntries(Map.entry("jobId", "-1"));
        Job job = getLatestSonarQubeJob(projectId);
        Integer jobId = job.getId();

        if (jobId > currentJobId) {
            Path artifact = Paths.get(QUALITY_GATE_PATH);
            Optional<InputStream> oStream = (Optional<InputStream>) makeRequestReturnOptional(
                    () -> client.getJobApi().downloadSingleArtifactsFile(projectId, jobId, artifact)
            );

            if (oStream.isPresent()) {
                InputStream stream = oStream.get();
                coverage = JsonMapper.getConditions(stream);
                coverage.put("jobId", jobId.toString());
                coverage.put("pipelineUrl", job.getPipeline().getWebUrl());
                coverage.put("pipelineStatus", job.getPipeline().getStatus().toString());
                coverage.put("triggeredBy", job.getUser().getUsername());
                coverage.put("ref", job.getRef());
            }
        }
        return coverage;
    }

    public Map<String, String> getJob(Integer projectId, Integer jobId) {
        Map<String, String> jobInfo = new HashMap<>();
        Job job =  (Job) makeRequest(
                () -> client.getJobApi().getJob(projectId, jobId));
        jobInfo.put("ref", job.getRef());
        jobInfo.put("pipelineUrl", job.getPipeline().getWebUrl());
        jobInfo.put("pipelineStatus", job.getStatus().toString());
        jobInfo.put("triggeredBy", job.getUser().getUsername());

        return jobInfo;
    }

    public Job getLatestSonarQubeJob(Integer projectId) {
        List<Job> jobs = (List<Job>) makeRequest(
                () -> client.getJobApi().getJobs(projectId, 1, 200));
        return jobs.stream()
                .filter(j -> j.getName().equals("sonarqube"))
                .findFirst()
                .orElseThrow(() -> new GitApiException("Job", "sonarqube"));
    }

    @FunctionalInterface
    private interface GitLabApiThunk<T> {
        T call() throws GitLabApiException, GitApiException;
    }

    @FunctionalInterface
    private interface GitLabApiThunkOptional<T> {
        T call();
    }

    private Object makeRequest(GitLabApiThunk<?> request) {
        try {
            return request.call();
        } catch (GitLabApiException e) {
            log.error(e.getMessage());
            throw new GitApiException(e.getLocalizedMessage());
        }
    }

    private Object makeRequestReturnOptional(GitLabApiThunk<?> request) {
        try {
            return Optional.of(request.call());
        } catch (GitLabApiException e) {
            return Optional.empty();
        }
    }

}
