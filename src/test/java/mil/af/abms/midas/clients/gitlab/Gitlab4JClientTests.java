package mil.af.abms.midas.clients.gitlab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.JobStatus;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.PipelineStatus;
import org.gitlab4j.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.Spy;

import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.epic.EpicRepository;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.config.SpringContext;
import mil.af.abms.midas.exception.GitApiException;

@ExtendWith(SpringExtension.class)
@Import(SpringContext.class)
class Gitlab4JClientTests {

    @Autowired
    SpringContext springContext;
    @MockBean
    SimpMessageSendingOperations websocket;
    @MockBean
    private EpicRepository repository;
    @MockBean
    private EpicService epicService;
    private static EpicService epicService() { return SpringContext.getBean(EpicService.class); }

    SimpMessageSendingOperations websocketMock = Mockito.mock(SimpMessageSendingOperations.class);
    @Spy
    GitLab4JClient gitClient = new GitLab4JClient("http://localhost", "token", websocketMock);

    User user = Builder.build(User.class)
            .with(u -> u.setUsername("fizzBang"))
            .get();
    Pipeline pipeline = Builder.build(Pipeline.class)
            .with(p -> p.setWebUrl("http://foo.bar"))
            .with(p -> p.setStatus(PipelineStatus.SUCCESS))
            .get();
    Job job = Builder.build(Job.class)
            .with(j -> j.setId(123456))
            .with(j -> j.setName("sonarqube"))
            .with(j -> j.setRef("master"))
            .with(j -> j.setPipeline(pipeline))
            .with(j -> j.setUser(user))
            .with(j -> j.setStatus(JobStatus.SUCCESS))
            .get();
    SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setId(3L))
            .with(sc -> sc.setToken("fake_token"))
            .with(sc -> sc.setBaseUrl("http://localhost:80"))
            .get();
    Product foundProduct = Builder.build(Product.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setGitlabGroupId(42))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setIsArchived(false))
            .get();
    Epic foundEpicForProduct = Builder.build(Epic.class)
            .with(e -> e.setId(6L))
            .with(e -> e.setTitle("title"))
            .with(e -> e.setEpicUid("3-42-42"))
            .with(e -> e.setEpicIid(42))
            .with(e -> e.setCompletedWeight(0L))
            .with(e -> e.setTotalWeight(0L))
            .with(e -> e.setProduct(foundProduct))
            .with((e -> e.setCompletions(Set.of())))
            .get();

    private static final String SONAR_URL = "foo\nyou can browse https://sonarqube";

    @Test
    void should_throw_on_empty_source_control_url() {
        var sc = new SourceControl();
        sc.setToken("fake token");

        assertThrows(IllegalArgumentException.class, () -> new GitLab4JClient(sc, websocket));
        assertThrows(IllegalArgumentException.class, () -> new GitLab4JClient(sc.getBaseUrl(), sc.getToken(), websocket));
    }

    @Test
    void should_throw_on_empty_source_control_token() {
        var sc = new SourceControl();
        sc.setBaseUrl("fake url");

        assertThrows(IllegalArgumentException.class, () -> new GitLab4JClient(sc, websocket));
        assertThrows(IllegalArgumentException.class, () -> new GitLab4JClient(sc.getBaseUrl(), sc.getToken(), websocket));
    }

    @Test
    void should_create_with_source_control() {
        var testClient = new GitLab4JClient(sourceControl, websocket);

        assertThat(testClient).hasFieldOrProperty("client");
    }

    @Test
    void should_return_false_on_projectExistsById() {
        doReturn(Optional.empty()).when(gitClient).makeRequest(any());
        assertFalse(gitClient.projectExistsById(123));
    }

    @Test
    void findProjectById_should_return_empty_on_exception() {
        doThrow(new GitApiException("F")).when(gitClient).makeRequest(any());

        assertThat(gitClient.findProjectById(1)).isEqualTo(Optional.empty());
    }

    @Test
    void should_return_false_on_epicExistsByIdAndGroupId() {
        doReturn(Optional.empty()).when(gitClient).makeRequest(any());
        assertFalse(gitClient.epicExistsByIdAndGroupId(123, 321));
    }

    @Test
    void should_return_false_on_issueExistsByIdAndProjectId() {
        doReturn(Optional.empty()).when(gitClient).makeRequest(any());
        assertFalse(gitClient.issueExistsByIdAndProjectId(123, 321));
    }

    @Test
    void findEpicByIdAndGroup_should_return_empty_on_exception() {
        doThrow(new GitApiException("F")).when(gitClient).makeRequest(any());

        assertThat(gitClient.findEpicByIdAndGroupId(1, 1)).isEqualTo(Optional.empty());
    }

    @Test
    void findIssueByIdAndProjectId_should_return_empty_on_exception() {
        doThrow(new GitApiException("F")).when(gitClient).makeRequest(any());

        assertThat(gitClient.findIssueByIdAndProjectId(1, 1)).isEqualTo(Optional.empty());
    }

    @Test
    void should_getLatestSonarQubeJob() {
        doReturn(List.of(job)).when(gitClient).makeRequest(any());

        Job jobLatest = gitClient.getLatestSonarQubeJob(3209);

        assertThat(jobLatest).isEqualTo(job);
    }

    @Test
    void should_return_null_getLatestSonarQubeJob() {
        doReturn(List.of()).when(gitClient).makeRequest(any());

        assertThat(gitClient.getLatestSonarQubeJob(1)).isEqualTo(null);
    }

    @Test
    void getLatestCodeCoverage_should_return_new_on_null_job() {
        var coverage = Map.ofEntries(Map.entry("jobId", "-1"));

        doReturn(null).when(gitClient).getLatestSonarQubeJob(any());

        assertThat(gitClient.getLatestCodeCoverage(1, 1)).isEqualTo(coverage);
    }

    @Test
    void should_return_jobId_negative_1_when_artifact_as_optional_stream_empty() {
        doReturn(job).when(gitClient).getLatestSonarQubeJob(any());
        doReturn(Optional.empty()).when(gitClient).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = gitClient.getLatestCodeCoverage(3209, 3209);

        assertThat(jobInfo).containsEntry("jobId", "-1");
    }

    @Test
    void should_return_jobId_negative_1_when_new_jobId_not_gt_current_jobId() {
        doReturn(job).when(gitClient).getLatestSonarQubeJob(any());
        doReturn(Optional.empty()).when(gitClient).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = gitClient.getLatestCodeCoverage(3209, 333209);

        assertThat(jobInfo).containsEntry("jobId", "-1");
    }

    @Test
    void should_return_artifact_as_optional_stream() throws Exception {
        String resourceName = "src/test/resources/condition.json";
        String conditionStr = Files.readString(Path.of(resourceName));
        InputStream stream = new ByteArrayInputStream(conditionStr.getBytes(StandardCharsets.UTF_8));

        doReturn(job).when(gitClient).getLatestSonarQubeJob(any());
        doReturn(Optional.of(stream)).when(gitClient).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = gitClient.getLatestCodeCoverage(3209, 0);

        assertThat(jobInfo)
                .containsEntry("ref", job.getRef())
                .containsEntry("pipelineStatus", job.getStatus().toString())
                .containsEntry("triggeredBy", user.getUsername())
                .containsEntry("pipelineUrl", pipeline.getWebUrl())
                .containsEntry("jobId", job.getId().toString());
    }

    @Test
    void should_getSonarqubeProjectUrl() {
        doReturn(Optional.of(new ByteArrayInputStream(SONAR_URL.getBytes(StandardCharsets.UTF_8))))
                .when(gitClient).makeRequestReturnOptional(any());
        assertThat(gitClient.getSonarqubeProjectUrl(1, 2)).isEqualTo("https://sonarqube");
    }

    @Test
    void should_make_request() {
        assertThat(gitClient.makeRequest(() -> "foo")).isEqualTo("foo");
    }

    @Test
    void should_throw_on_make_request() {
        assertThrows(GitApiException.class, () -> gitClient.makeRequest(() -> { throw new GitLabApiException("foo"); }));
    }

    @Test
    void should_make_optional_request() {
        assertThat(gitClient.makeRequestReturnOptional(() -> "foo")).isEqualTo(Optional.of("foo"));
    }

    @Test
    void should_return_empty_on_make_optional_request() {
        assertThat(gitClient.makeRequestReturnOptional(() -> { throw new GitLabApiException("foo"); })).isEqualTo(Optional.empty());
    }

    @ParameterizedTest
    @CsvSource(value = { "OK: true", "BAD_REQUEST: false" }, delimiter = ':')
    void should_get_Epic_from_Api(String status, boolean isOk) {
        ResponseEntity<String> testResponse = new ResponseEntity<>("{ \"iid\": 42}", HttpStatus.valueOf(status));

        doReturn(testResponse).when(gitClient).requestGet(anyString());

        if (isOk) {
            assertThat(gitClient.getEpicFromGroup(1, 2).getEpicIid()).isEqualTo(42);
        } else {
            assertThrows(HttpClientErrorException.class, () -> gitClient.getEpicFromGroup(1, 2));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "OK; [{\"iid\":42}]", "BAD_REQUEST; [{}]", "OK; ---" }, delimiter = ';')
    void should_get_page_of_Epics_from_Api(String status, String response) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("x-total-pages", "1");

        ResponseEntity<String> testResponse = new ResponseEntity<>(response, headers, HttpStatus.valueOf(status));
        doReturn(testResponse).when(gitClient).requestGet(anyString());
        when(repository.findByEpicUid("3-42-42")).thenReturn(Optional.of(foundEpicForProduct));
        doReturn(Set.of(foundEpicForProduct)).when(epicService()).processEpics(any(), any());

        if (response.equals("[{\"iid\":42}]")) {
            assertThat(gitClient.fetchGitLabEpicByPage(foundProduct, 1).iterator().next().getEpicIid()).isEqualTo(42);
        } else if (response.equals("---")) {
            assertThrows(GitApiException.class, () ->  gitClient.fetchGitLabEpicByPage(foundProduct, 1));
        } else {
            assertThrows(HttpClientErrorException.class, () -> gitClient.fetchGitLabEpicByPage(foundProduct, 1));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "OK; [{\"iid\":42}]", "BAD_REQUEST; [{}]", "OK; ---" }, delimiter = ';')
    void should_get_sub_epics_from_Api(String status, String response) {
        ResponseEntity<String> testResponse = new ResponseEntity<>(response, HttpStatus.valueOf(status));
        doReturn(testResponse).when(gitClient).requestGet(anyString());

        if (response.equals("[{\"iid\":42}]")) {
            assertThat(gitClient.getSubEpicsFromEpicAndGroup(1, 42).get(0).getEpicIid()).isEqualTo(42);
        } else if (response.equals("---")) {
            assertThrows(GitApiException.class, () ->  gitClient.getSubEpicsFromEpicAndGroup(1, 42));
        } else {
            assertThrows(HttpClientErrorException.class, () -> gitClient.getSubEpicsFromEpicAndGroup(1, 42));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "OK: true", "BAD_REQUEST: false" }, delimiter = ':')
    void should_get_Issue_from_Api(String status, boolean isOk) {
        ResponseEntity<String> testResponse = new ResponseEntity<>("{ \"iid\": 42}", HttpStatus.valueOf(status));

        doReturn(testResponse).when(gitClient).requestGet(anyString());

        if (isOk) {
            assertThat(gitClient.getIssueFromProject(1, 2).getIssueIid()).isEqualTo(42);
        } else {
            assertThrows(HttpClientErrorException.class, () -> gitClient.getIssueFromProject(1, 2));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "OK; [{\"iid\":42}]", "BAD_REQUEST; [{}]", "OK; ---" }, delimiter = ';')
    void should_get_Issues_from_Api(String status, String response) {
        ResponseEntity<String> testResponse = new ResponseEntity<>(response, HttpStatus.valueOf(status));
        doReturn(testResponse).when(gitClient).requestGet(anyString());

        if (response.equals("[{\"iid\":42}]")) {
            assertThat(gitClient.getIssuesFromProject(1).get(0).getIssueIid()).isEqualTo(42);
        } else if (response.equals("---")) {
            assertThrows(GitApiException.class, () ->  gitClient.getIssuesFromProject(1));
        } else {
            assertThrows(HttpClientErrorException.class, () -> gitClient.getIssuesFromProject(1));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "OK; [{\"iid\":42}]", "BAD_REQUEST; [{}]", "OK; ---" }, delimiter = ';')
    void should_get_Issues_from_Epic_Api(String status, String response) {
        ResponseEntity<String> testResponse = new ResponseEntity<>(response, HttpStatus.valueOf(status));
        doReturn(testResponse).when(gitClient).requestGet(anyString());

        if (response.equals("[{\"iid\":42}]")) {
            assertThat(gitClient.getIssuesFromEpic(1, 42).get(0).getIssueIid()).isEqualTo(42);
        } else if (response.equals("---")) {
            assertThrows(GitApiException.class, () ->  gitClient.getIssuesFromEpic(1, 42));
        } else {
            assertThrows(HttpClientErrorException.class, () -> gitClient.getIssuesFromEpic(1, 42));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "OK; [{\"id\":42}]", "BAD_REQUEST; [{}]", "OK; ---" }, delimiter = ';')
    void should_get_Projects_from_Group(String status, String response) {
        ResponseEntity<String> testResponse = new ResponseEntity<>(response, HttpStatus.valueOf(status));
        doReturn(testResponse).when(gitClient).requestGet(anyString());

        if (response.equals("[{\"id\":42}]")) {
            assertThat(gitClient.getProjectsFromGroup(1).get(0).getGitlabProjectId()).isEqualTo(42);
        } else if (response.equals("---")) {
            assertThrows(GitApiException.class, () ->  gitClient.getProjectsFromGroup(1));
        } else {
            assertThrows(HttpClientErrorException.class, () -> gitClient.getProjectsFromGroup(1));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "OK: true", "BAD_REQUEST: false" }, delimiter = ':')
    void should_sync_Project_from_Gitlab(String status, boolean isOk) {
        ResponseEntity<String> testResponse = new ResponseEntity<>("{ \"id\": 42}", HttpStatus.valueOf(status));
        doReturn(testResponse).when(gitClient).requestGet(anyString());

        if (isOk) {
            assertThat(gitClient.getGitLabProject(42).getGitlabProjectId()).isEqualTo(42);
        } else {
            assertThrows(HttpClientErrorException.class, () -> gitClient.getGitLabProject(42));
        }
    }

    @Test
    void should_throw_request_get() {
        assertThat(gitClient.requestGet("fake_url").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_request_get() {
        assertThat(gitClient.requestGet("https://www.google.com/").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
