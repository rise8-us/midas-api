package mil.af.abms.midas.clients;

import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    public Optional<Project> findProjectById(Long id) {
        return (Optional<Project>) makeOptionalGitLabApiRequest(() -> client.getProjectApi().getOptionalProject(id));
    }

    public Boolean projectExistsById(Long id) {
        return findProjectById(id).isPresent();
    }

    public Project getProjectById(Long id) {
        return findProjectById(id).orElseThrow(() -> new GitApiException(Project.class.getSimpleName(), id));
    }

    public Optional<User> findUserByUsername(String username) {
        return (Optional<User>) makeOptionalGitLabApiRequest(() -> client.getUserApi().getOptionalUser(username));
    }

    public User getUserByUsername(String username) {
        return findUserByUsername(username).orElseThrow(() -> new GitApiException(User.class.getSimpleName(), username));
    }

    public Map<String, String> getLatestCodeCoverage(Integer projectId) {
        Integer jobId = getLatestSonarQubeJob(projectId).getId();
        Path artifact = Paths.get(QUALITY_GATE_PATH);
        InputStream stream = (InputStream) makeGitLabApiRequest(
                () -> client.getJobApi().downloadSingleArtifactsFile(projectId, jobId, artifact)
        );
        return JsonMapper.getConditions(stream);
    }

    public Job getLatestSonarQubeJob(Integer projectId) {
        List<Job> jobs = (List<Job>) makeGitLabApiRequest(
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

    private Object makeGitLabApiRequest(GitLabApiThunk<?> request) {
        try {
            return request.call();
        } catch (GitLabApiException e) {
            log.error(e.getMessage());
            throw new GitApiException(e.getLocalizedMessage());
        }
    }

    private Object makeOptionalGitLabApiRequest(GitLabApiThunkOptional<?> request) {
            return request.call();
    }

}
