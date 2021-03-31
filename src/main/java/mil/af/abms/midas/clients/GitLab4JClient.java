package mil.af.abms.midas.clients;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.User;

import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.exception.GitApiException;

@Slf4j
@SuppressWarnings("unchecked")
public class GitLab4JClient {
    private final GitLabApi client;

    public GitLab4JClient(@Autowired CustomProperty property) {
        this.client = new GitLabApi(property.getGitLabUrl(), property.getGitLabAccessToken());
    }

    public Optional<Project> findProjectById(Long id) {
        return (Optional<Project>) makeGitLabApiRequest(() -> client.getProjectApi().getOptionalProject(id));
    }

    public Boolean projectExistsById(Long id) {
        return findProjectById(id).isPresent();
    }

    public Project getProjectById(Long id) {
        return findProjectById(id).orElseThrow(() -> new GitApiException(Project.class.getSimpleName(), id));
    }

    public Optional<User> findUserByUsername(String username) {
        return (Optional<User>) makeGitLabApiRequest(() -> client.getUserApi().getOptionalUser(username));
    }

    public User getUserByUsername(String username) {
        return findUserByUsername(username).orElseThrow(() -> new GitApiException(User.class.getSimpleName(), username));
    }

    @FunctionalInterface
    private interface GitLabApiThunk<T> {
        T call() throws GitLabApiException, GitApiException;
    }

    private Object makeGitLabApiRequest(GitLabApiThunk<?> request) {
        try {
            return request.call();
        } catch (GitLabApiException e) {
            log.error(e.getMessage());
            return Optional.empty();
        }
    }

}
