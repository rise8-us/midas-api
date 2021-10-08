package mil.af.abms.midas.clients.gitlab;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.mockito.Spy;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.exception.GitApiException;

@ExtendWith(SpringExtension.class)
class Gitlab4JClientTests {

    @Spy
    GitLab4JClient client = new GitLab4JClient("url", "token");

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
            .with(sc -> sc.setBaseUrl("fake_url"))
            .get();

    private static final String SONAR_URL = "foo\nyou can browse https://sonarqube";

    @Test
    void should_return_false_on_projectExistsById() {
        doReturn(Optional.empty()).when(client).makeRequest(any());
        assertFalse(client.projectExistsById(123));
    }

    @Test
    void should_return_false_on_epicExistsByIdAndGroupId() {
        doReturn(Optional.empty()).when(client).makeRequest(any());
        assertFalse(client.epicExistsByIdAndGroupId(123, 321));
    }

    @Test
    void should_getLatestSonarQubeJob() {
        doReturn(List.of(job)).when(client).makeRequest(any());

        Job jobLatest = client.getLatestSonarQubeJob(3209);

        assertThat(jobLatest).isEqualTo(job);
    }

    @Test
    void should_throw_on_getLatestSonarQubeJob() {
        doReturn(List.of()).when(client).makeRequest(any());

        assertThrows(GitApiException.class, () -> client.getLatestSonarQubeJob(1));
    }

    @Test
    void should_return_jobId_negative_1_when_artifact_as_optional_stream_empty() {
        doReturn(job).when(client).getLatestSonarQubeJob(any());
        doReturn(Optional.empty()).when(client).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = client.getLatestCodeCoverage(3209, 3209);

        assertThat(jobInfo).containsEntry("jobId", "-1");
    }

    @Test
    void should_return_jobId_negative_1_when_new_jobId_not_gt_current_jobId() {
        doReturn(job).when(client).getLatestSonarQubeJob(any());
        doReturn(Optional.empty()).when(client).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = client.getLatestCodeCoverage(3209, 333209);

        assertThat(jobInfo).containsEntry("jobId", "-1");
    }

    @Test
    void should_return_artifact_as_optional_stream() throws Exception {
        String resourceName = "src/test/resources/condition.json";
        String conditionStr = Files.readString(Path.of(resourceName));
        InputStream stream = new ByteArrayInputStream(conditionStr.getBytes(StandardCharsets.UTF_8));

        doReturn(job).when(client).getLatestSonarQubeJob(any());
        doReturn(Optional.of(stream)).when(client).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = client.getLatestCodeCoverage(3209, 0);

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
                .when(client).makeRequestReturnOptional(any());
        assertThat(client.getSonarqubeProjectUrl(1, 2)).isEqualTo("https://sonarqube");
    }

    @Test
    void should_make_request() {
        assertThat(client.makeRequest(() -> "foo")).isEqualTo("foo");
    }

    @Test
    void should_throw_on_make_request() {
        assertThrows(GitApiException.class, () -> client.makeRequest(() -> { throw new GitLabApiException("foo"); }));
    }

    @Test
    void should_make_optional_request() {
        assertThat(client.makeRequestReturnOptional(() -> "foo")).isEqualTo(Optional.of("foo"));
    }

    @Test
    void should_return_empty_on_make_optional_request() {
        assertThat(client.makeRequestReturnOptional(() -> { throw new GitLabApiException("foo"); })).isEqualTo(Optional.empty());
    }

    @ParameterizedTest
    @CsvSource(value = { "OK: true", "BAD_REQUEST: false" }, delimiter = ':')
    void should_get_Epic_from_Api(String status, boolean isOk) {
        ResponseEntity<String> testResponse = new ResponseEntity<>("{ \"iid\": 42}", HttpStatus.valueOf(status));

        doReturn(testResponse).when(client).requestGet(anyString(), anyString());

        if(isOk) {
            assertThat(client.getEpicFromGroup(sourceControl, 1, 2).getEpicIid()).isEqualTo(42);
        } else {
            assertThrows(HttpClientErrorException.class, () -> client.getEpicFromGroup(sourceControl, 1, 2));
        }
    }

    @ParameterizedTest
    @CsvSource(value = { "OK; [{\"iid\":42}]", "BAD_REQUEST; [{}]", "OK; ---" }, delimiter = ';')
    void should_get_Epics_from_Api(String status, String response) {
        ResponseEntity<String> testResponse = new ResponseEntity<>(response, HttpStatus.valueOf(status));
        doReturn(testResponse).when(client).requestGet(anyString(), anyString());

        if(response.equals("[{\"iid\":42}]")) {
            assertThat(client.getEpicsFromGroup(sourceControl, 1).get(0).getEpicIid()).isEqualTo(42);
        } else if (response.equals("---")){
            assertThrows(GitApiException.class, () ->  client.getEpicsFromGroup(sourceControl, 1));
        } else {
            assertThrows(HttpClientErrorException.class, () -> client.getEpicsFromGroup(sourceControl, 1));
        }
    }

    @Test
    void should_throw_request_get() {
        assertThat(client.requestGet("fake_token", "fake_url").getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_request_get() {
        assertThat(client.requestGet("fake_token", "https://www.google.com/").getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}
