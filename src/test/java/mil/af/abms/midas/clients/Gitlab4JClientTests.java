package mil.af.abms.midas.clients;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Job;
import org.gitlab4j.api.models.JobStatus;
import org.gitlab4j.api.models.Pipeline;
import org.gitlab4j.api.models.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.config.CustomProperty;
import mil.af.abms.midas.exception.GitApiException;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(CustomProperty.class)
@Import(GitLab4JClient.class)
@TestPropertySource(locations = "classpath:customPropertyTest.properties")
public class Gitlab4JClientTests {


    @Autowired
    CustomProperty property;
    @SpyBean
    GitLab4JClient client;

    User user = Builder.build(User.class)
            .with(u -> u.setUsername("fizzBang"))
            .get();
    Pipeline pipeline = Builder.build(Pipeline.class)
            .with(p -> p.setWebUrl("http://foo.bar"))
            .get();
    Job job = Builder.build(Job.class)
            .with(j -> j.setId(123456))
            .with(j -> j.setName("sonarqube"))
            .with(j -> j.setRef("master"))
            .with(j -> j.setPipeline(pipeline))
            .with(j -> j.setUser(user))
            .with(j -> j.setStatus(JobStatus.SUCCESS))
            .get();

    @Test
    public void should_return_false_on_projectExistsById() {
        doReturn(Optional.empty()).when(client).makeRequest(any());
        assertFalse(client.projectExistsById(123));
    }

    @Test
    public void should_get_jobInfo() {
        doReturn(job).when(client).makeRequest(any(GitLab4JClient.GitLabApiThunk.class));
        Map<String, String> jobInfo = client.getJobInfo(3209, 14);

        assertThat(jobInfo.get("ref")).isEqualTo(job.getRef());
        assertThat(jobInfo.get("pipelineStatus")).isEqualTo(job.getStatus().toString());
        assertThat(jobInfo.get("triggeredBy")).isEqualTo(user.getUsername());
        assertThat(jobInfo.get("pipelineUrl")).isEqualTo(pipeline.getWebUrl());

    }

    @Test
    public void should_getLatestSonarQubeJob() {
        doReturn(List.of(job)).when(client).makeRequest(any());

        Job jobLatest = client.getLatestSonarQubeJob(3209);

        assertThat(jobLatest).isEqualTo(job);
    }

    @Test
    public void should_throw_on_getLatestSonarQubeJob() {
        doReturn(List.of()).when(client).makeRequest(any());

        assertThrows(GitApiException.class, () -> client.getLatestSonarQubeJob(any()));
    }

    @Test
    public void should_return_jobId_negative_1_when_artifact_as_optional_stream_empty() {
        doReturn(job).when(client).getLatestSonarQubeJob(any());
        doReturn(Optional.empty()).when(client).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = client.getLatestCodeCoverage(3209, 3209);

        assertThat(jobInfo.get("jobId")).isEqualTo("-1");
    }

    @Test
    public void should_return_jobId_negative_1_when_new_jobId_not_gt_current_jobId() {
        doReturn(job).when(client).getLatestSonarQubeJob(any());
        doReturn(Optional.empty()).when(client).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = client.getLatestCodeCoverage(3209, 333209);

        assertThat(jobInfo.get("jobId")).isEqualTo("-1");
    }

    @Test
    public void should_return_artifact_as_optional_stream() throws Exception {
        String resourceName = "src/test/resources/condition.json";
        String conditionStr = Files.readString(Path.of(resourceName));
        InputStream stream = new ByteArrayInputStream(conditionStr.getBytes(StandardCharsets.UTF_8));

        doReturn(job).when(client).getLatestSonarQubeJob(any());
        doReturn(Optional.of(stream)).when(client).makeRequestReturnOptional(any());

        Map<String, String> jobInfo = client.getLatestCodeCoverage(3209, 0);

        assertThat(jobInfo.get("ref")).isEqualTo(job.getRef());
        assertThat(jobInfo.get("pipelineStatus")).isEqualTo(job.getStatus().toString());
        assertThat(jobInfo.get("triggeredBy")).isEqualTo(user.getUsername());
        assertThat(jobInfo.get("pipelineUrl")).isEqualTo(pipeline.getWebUrl());
        assertThat(jobInfo.get("jobId")).isEqualTo(job.getId().toString());
    }

    @Test
    public void should_make_request() {
        assertThat(client.makeRequest(() -> "foo")).isEqualTo("foo");
    }

    @Test
    public void should_throw_on_make_request() {
        assertThrows(GitApiException.class, () -> client.makeRequest(() -> { throw new GitLabApiException("foo"); }));
    }

    @Test
    public void should_make_optional_request() {
        assertThat(client.makeRequestReturnOptional(() -> "foo")).isEqualTo(Optional.of("foo"));
    }

    @Test
    public void should_return_empty_on_make_optional_request() {
        assertThat(client.makeRequestReturnOptional(() -> { throw new GitLabApiException("foo"); })).isEqualTo(Optional.empty());
    }

}
