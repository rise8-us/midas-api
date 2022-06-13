package mil.af.abms.midas.clients.gitlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;

import mil.af.abms.midas.api.AppGroup;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.api.issue.Issue;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;
import mil.af.abms.midas.clients.gitlab.models.GitLabProject;
import mil.af.abms.midas.exception.GitApiException;

@Slf4j
@SuppressWarnings("unchecked")
public class GitLab4JClient {

    private static final String QUALITY_GATE_PATH = ".ci_artifacts/sonarqube/report_qualitygate_status.json";
    private static final String SONAR_LOG_PATH = ".ci_artifacts/sonarqube/sonar-scanner.log";
    private static final String GET_EPICS_ERROR_MESSAGE = "Unable to map Gitlab Epic Json to Midas Epic";
    private static final String GET_ISSUES_ERROR_MESSAGE = "Unable to map Gitlab Issue Json to Midas Issue";

    private final String baseUrl;
    private final String token;
    private final GitLabApi client;

    public GitLab4JClient(String url, String token) {
        this.baseUrl = Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("A gitlab url must be provided"));
        this.token = Optional.ofNullable(token).orElseThrow(() -> new IllegalArgumentException("a gitlab token must be provided"));
        this.client = new GitLabApi(this.baseUrl, this.token);
    }

    public GitLab4JClient(SourceControl sourceControl) {
        this.baseUrl = Optional.ofNullable(sourceControl.getBaseUrl()).orElseThrow(() -> new IllegalArgumentException("A gitlab url must be provided"));
        this.token = Optional.ofNullable(sourceControl.getToken()).orElseThrow(() -> new IllegalArgumentException("a gitlab token must be provided"));
        this.client = new GitLabApi(this.baseUrl, this.token);
    }

    public Optional<Project> findProjectById(Integer id) {
        try {
            return (Optional<Project>) makeRequest(() -> client.getProjectApi().getOptionalProject(id));
        } catch (GitApiException e) {
            log.error(String.format("GitLab4JClient could not find gitlab project with id: %d", id));
            return Optional.empty();
        }
    }

    public Optional<Epic> findEpicByIdAndGroupId(Integer id, Integer groupId) {
        try {
            return (Optional<Epic>) makeRequest(() -> client.getEpicsApi().getOptionalEpic(groupId, id));
        } catch (GitApiException e) {
            log.error(String.format("GitLab4JClient could not find epic with id: %d in group: %d", id, groupId));
            return Optional.empty();
        }
    }

    public Optional<Issue> findIssueByIdAndProjectId(Integer id, Integer projectId) {
        try {
            return (Optional<Issue>) makeRequest(() -> client.getIssuesApi().getOptionalIssue(projectId, id));
        } catch (GitApiException e) {
            log.error(String.format("GitLab4JClient could not find issue with id: %d in project: %d", id, projectId));
            return Optional.empty();
        }
    }

    public Boolean projectExistsById(Integer id) {
        return findProjectById(id).isPresent();
    }

    public Boolean epicExistsByIdAndGroupId(Integer id, Integer groupId) {
        return findEpicByIdAndGroupId(id, groupId).isPresent();
    }

    public Boolean issueExistsByIdAndProjectId(Integer id, Integer projectId) {
        return findIssueByIdAndProjectId(id, projectId).isPresent();
    }

    public Map<String, String> getLatestCodeCoverage(Integer projectId, Integer currentJobId) {
        var coverage = Map.ofEntries(Map.entry("jobId", "-1"));
        var job = getLatestSonarQubeJob(projectId);
        if (job == null) {
            return coverage;
        }

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
        try {
            List<Job> jobs = (List<Job>) makeRequest(() -> client.getJobApi().getJobs(projectId, 1, 200));
            return jobs.stream()
                    .filter(j -> j.getName().equals("sonarqube") && j.getRef().equals("master"))
                    .max(Comparator.comparing(Job::getId))
                    .orElseThrow(() -> new GitApiException("Job", "sonarqube"));
        } catch (GitApiException e) {
            log.error(String.format("GitLab4JClient could not get latest sonarqube job for gitlab project with id: %d", projectId));
            return null;
        }
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

    public int getTotalEpicsPages(AppGroup appGroup) {
        Integer groupId = appGroup.getGitlabGroupId();
        String url = String.format("%s/api/v4/groups/%d/epics?include_descendant_groups=false&pagination=keyset&per_page=20", this.baseUrl, groupId);
        ResponseEntity<String> response = requestGet(url);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                return Integer.parseInt(Objects.requireNonNull(response.getHeaders().get("x-total-pages")).get(0));
            }
            catch (Exception e) {
                throw new GitApiException(GET_EPICS_ERROR_MESSAGE);
            }
        }
        return -1;
    }

    public List<GitLabEpic> getSubEpicsFromEpicAndGroup(Integer groupId, Integer iid) {
        String url = String.format("%s/api/v4/groups/%d/epics/%d/epics", this.baseUrl, groupId, iid);
        return getGitLabEpics(url);
    }

    private void fetchGitLabEpics(ArrayList<GitLabEpic> epics, ResponseEntity<String> response) throws IOException {
        var epicArray = JsonMapper.dateMapper().readTree(response.getBody());
        for (var epic : epicArray) {
            epics.add(mapEpicFromJson(epic.toString()));
        }
    }

    private void fetchGitLabIssues(ArrayList<GitLabIssue> issues, ResponseEntity<String> response) throws IOException {
        var issueArray = JsonMapper.dateMapper().readTree(response.getBody());
        for (var issue : issueArray) {
            issues.add(mapIssueFromJson(issue.toString()));
        }
    }

    public List<GitLabEpic> fetchGitLabEpicByPage(AppGroup appGroup, Integer page) {
        Integer groupId = appGroup.getGitlabGroupId();
        String url = String.format("%s/api/v4/groups/%d/epics?include_descendant_groups=false&pagination=keyset&per_page=20&page=%d", this.baseUrl, groupId, page);
        ResponseEntity<String> response = requestGet(url);
        ArrayList<GitLabEpic> epics = new ArrayList<>();
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                JsonNode jsonEpicNode = JsonMapper.dateMapper().readTree(response.getBody());
                for (JsonNode epic : jsonEpicNode) {
                    epics.add(mapEpicFromJson(epic.toString()));
                }
            }
            catch (IOException e) {
                throw new GitApiException(GET_EPICS_ERROR_MESSAGE);
            }
        } else {
            throw new HttpClientErrorException(response.getStatusCode());
        }
        return epics;
    }

    public List<GitLabIssue> fetchGitLabIssueByPage(Project project, Integer page) {
        Integer gitlabProjectId = project.getGitlabProjectId();
        String url = String.format("%s/api/v4/projects/%d/issues?pagination=keyset&per_page=20&page=%d", this.baseUrl, gitlabProjectId, page);
        ResponseEntity<String> response = requestGet(url);
        ArrayList<GitLabIssue> issues = new ArrayList<>();
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                JsonNode jsonIssueNode = JsonMapper.dateMapper().readTree(response.getBody());
                for (JsonNode issue : jsonIssueNode) {
                    issues.add(mapIssueFromJson(issue.toString()));
                }
            }
            catch (IOException e) {
                throw new GitApiException(GET_ISSUES_ERROR_MESSAGE);
            }
        } else {
            throw new HttpClientErrorException(response.getStatusCode());
        }
        return issues;
    }

    private List<GitLabEpic> getGitLabEpics(String url) {
        ResponseEntity<String> response = requestGet(url);
        var epics = new ArrayList<GitLabEpic>();

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                fetchGitLabEpics(epics, response);
            } catch (Exception e) {
                throw new GitApiException(GET_EPICS_ERROR_MESSAGE);
            }
        } else {
            throw new HttpClientErrorException((response.getStatusCode()));
        }

        return epics;
    }

    private List<GitLabIssue> getGitLabIssues(String url) {
        ResponseEntity<String> response = requestGet(url);
        var issues = new ArrayList<GitLabIssue>();
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                fetchGitLabIssues(issues, response);
            } catch (Exception e) {
                throw new GitApiException(GET_ISSUES_ERROR_MESSAGE);
            }
        } else {
            throw new HttpClientErrorException(response.getStatusCode());
        }

        return issues;
    }

    public List<GitLabIssue> getIssuesFromEpic(Integer groupId, Integer epicIid) {
        String url = String.format("%s/api/v4/groups/%d/epics/%d/issues?pagination=keyset&per_page=100", this.baseUrl, groupId, epicIid);
        return getGitLabIssues(url);
    }

    public List<GitLabIssue> getIssuesFromProject(Integer projectId) {
        String url = String.format("%s/api/v4/projects/%d/issues?pagination=keyset&per_page=100", this.baseUrl, projectId);
        return getGitLabIssues(url);
    }

    public GitLabEpic getEpicFromGroup(Integer groupId, Integer iid) {
        String url = String.format("%s/api/v4/groups/%d/epics/%d", this.baseUrl, groupId, iid);
        ResponseEntity<String> response = requestGet(url);

        if (response.getStatusCode().equals(HttpStatus.OK))
            return mapEpicFromJson(response.getBody());
        else {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    public GitLabIssue getIssueFromProject(Integer projectId, Integer iid) {
        String url = String.format("%s/api/v4/projects/%d/issues/%d", this.baseUrl, projectId, iid);
        ResponseEntity<String> response = requestGet(url);

        if (response.getStatusCode().equals(HttpStatus.OK))
            return mapIssueFromJson(response.getBody());
        else {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    protected GitLabIssue mapIssueFromJson(String body) {
        try {
             return JsonMapper.dateMapper()
                    .readerFor(GitLabIssue.class)
                    .readValue(body);
        } catch (IOException e) {
            throw new GitApiException(GET_ISSUES_ERROR_MESSAGE);
        }

    }

    protected GitLabEpic mapEpicFromJson(String body) {
        try {
             return JsonMapper.dateMapper()
                    .readerFor(GitLabEpic.class)
                    .readValue(body);
        } catch (IOException e) {
            throw new GitApiException(GET_EPICS_ERROR_MESSAGE);
        }

    }

    public GitLabProject getGitLabProject(Integer gitLabProjectId) {
        String url = String.format("%s/api/v4/projects/%d", this.baseUrl, gitLabProjectId);
        ResponseEntity<String> response = requestGet(url);
        if (response.getStatusCode().equals(HttpStatus.OK))
            return mapProjectFromJson(response.getBody());
        else {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    public List<GitLabProject> getProjectsFromGroup(Integer groupId) {
        String url = String.format("%s/api/v4/groups/%d/projects", this.baseUrl, groupId);
        ResponseEntity<String> response = requestGet(url);
        var projects = new ArrayList<GitLabProject>();

        if (response.getStatusCode().equals(HttpStatus.OK))
            try {
                var projectArray = JsonMapper.dateMapper().readTree(response.getBody());
                for (var project : projectArray) {
                    projects.add(mapProjectFromJson(project.toString()));
                }
                return projects;
            } catch (IOException e) {
                throw new GitApiException("Unable to map Gitlab Project Json to Midas Project");
            }
        else {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    protected GitLabProject mapProjectFromJson(String body) {
        try {
            return JsonMapper.dateMapper()
                    .readerFor(GitLabProject.class)
                    .readValue(body);
        } catch (IOException e) {
            throw new GitApiException("Unable to map Gitlab Project Json to Midas Project");
        }
    }

    protected ResponseEntity<String> requestGet(String endpoint) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", this.token);
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        try {
            return restTemplate.exchange(
                    endpoint,
                    HttpMethod.GET,
                    request,
                    String.class
            );
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    public int getTotalIssuesPages(Project project) {
        Integer gitlabProjectId = project.getGitlabProjectId();
        String url = String.format("%s/api/v4/projects/%d/issues?pagination=keyset&per_page=20", this.baseUrl, gitlabProjectId);
        ResponseEntity<String> response = requestGet(url);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            try {
                return Integer.parseInt(Objects.requireNonNull(response.getHeaders().get("x-total-pages")).get(0));
            }
            catch (Exception e) {
                throw new GitApiException(GET_ISSUES_ERROR_MESSAGE);
            }
        }
        return -1;
    }



    @FunctionalInterface
    protected interface GitLabApiThunk<T> {
        T call() throws GitLabApiException, GitApiException;
    }

    protected Object makeRequest(GitLabApiThunk<?> request) {
        try {
            return request.call();
        } catch (GitLabApiException e) {
            log.error(String.format("GitLab4JClient makeRequest failed with error: %s", e.getMessage()));
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
