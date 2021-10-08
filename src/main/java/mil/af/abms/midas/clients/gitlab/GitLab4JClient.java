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
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Epic;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.Project;

import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.models.EpicConversion;
import mil.af.abms.midas.exception.GitApiException;

@Slf4j
@NoArgsConstructor
@SuppressWarnings("unchecked")
public class GitLab4JClient {

    private static final String QUALITY_GATE_PATH = ".ci_artifacts/sonarqube/report_qualitygate_status.json";
    private static final String SONAR_LOG_PATH = ".ci_artifacts/sonarqube/sonar-scanner.log";
    private GitLabApi client;

    public GitLab4JClient(String url, String token) {
        url = Optional.ofNullable(url).orElseThrow(() -> new IllegalArgumentException("A gitlab url must be provided"));
        token = Optional.ofNullable(token).orElseThrow(() -> new IllegalArgumentException("a gitlab token must be provided"));
        this.client = new GitLabApi(url, token);
    }

    public Optional<Project> findProjectById(Integer id) {
        return (Optional<Project>) makeRequest(() -> client.getProjectApi().getOptionalProject(id));
    }

    public Optional<Epic> findEpicByIdAndGroupId(Integer id, Integer groupId) {
        return (Optional<Epic>) makeRequest(() -> client.getEpicsApi().getOptionalEpic(groupId, id));
    }

    public Boolean projectExistsById(Integer id) {
        return findProjectById(id).isPresent();
    }

    public Boolean epicExistsByIdAndGroupId(Integer id, Integer groupId) {
        return findEpicByIdAndGroupId(id, groupId).isPresent();
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
                .filter(j -> j.getName().equals("sonarqube") && j.getRef().equals("master"))
                .max(Comparator.comparing(Job::getId))
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

    public List<EpicConversion> getEpicsFromGroup(SourceControl sourceControl, Integer groupId) {
        String url = String.format("%s/api/v4/groups/%d/epics", sourceControl.getBaseUrl(), groupId);
        ResponseEntity<String> response = requestGet(sourceControl.getToken(), url);
        var epics = new ArrayList<EpicConversion>();

        if (response.getStatusCode().equals(HttpStatus.OK))
            try {
                var epicArray = JsonMapper.dateMapper().readTree(response.getBody());
                for(var epic : epicArray) {
                    epics.add(mapEpicFromJson(epic.toString()));
                }
               return epics;
            } catch (IOException e) {
                throw new GitApiException("Unable to map Gitlab Epic Json to Midas Epic");
            }
        else {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    public EpicConversion getEpicFromGroup(SourceControl sourceControl, Integer groupId, Integer iid) {
        String url = String.format("%s/api/v4/groups/%d/epics/%d", sourceControl.getBaseUrl(), groupId, iid);
        ResponseEntity<String> response = requestGet(sourceControl.getToken(), url);

        if (response.getStatusCode().equals(HttpStatus.OK))
            return mapEpicFromJson(response.getBody());
        else {
            throw new HttpClientErrorException(response.getStatusCode());
        }
    }

    protected EpicConversion mapEpicFromJson(String body) {
        try {
             return JsonMapper.dateMapper()
                    .readerFor(EpicConversion.class)
                    .readValue(body);
        } catch (IOException e) {
            throw new GitApiException("Unable to map Gitlab Epic Json to Midas Epic");
        }

    }

    protected ResponseEntity<String> requestGet(String token, String endpoint) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("PRIVATE-TOKEN", token);
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
