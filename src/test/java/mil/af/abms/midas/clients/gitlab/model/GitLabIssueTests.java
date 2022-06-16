package mil.af.abms.midas.clients.gitlab.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;

class GitLabIssueTests {

    @Test
    void can_map_from_JSON() throws IOException {
        String resourceName = "src/test/resources/gitlab/issue.json";
        String conditionStr = Files.readString(Path.of(resourceName));
        InputStream stream = new ByteArrayInputStream(conditionStr.getBytes(StandardCharsets.UTF_8));

        GitLabIssue issue = JsonMapper.dateMapper()
                .readerFor(GitLabIssue.class)
                .readValue(stream);

       assertThat(issue.getTitle()).isEqualTo("Consequatur vero maxime deserunt laboriosam est voluptas dolorem.");
       assertThat(issue.getDescription()).isEqualTo("Ratione dolores corrupti mollitia soluta quia.");
       assertThat(issue.getCreationDate()).isEqualTo(LocalDateTime.parse("2016-01-04T15:31:51.081"));
       assertThat(issue.getDueDate()).isEqualTo(LocalDate.parse("2016-07-22"));
       assertThat(issue.getCompletedAt()).isNull();
       assertThat(issue.getUpdatedAt()).isEqualTo(LocalDateTime.parse("2016-01-04T15:31:51.081"));
       assertThat(issue.getIssueIid()).isEqualTo(6);
       assertThat(issue.getState()).isEqualTo("opened");
       assertThat(issue.getWebUrl()).isEqualTo("http://gitlab.example.com/my-group/my-project/issues/6");
       assertThat(issue.getWeight()).isNull();
       assertThat(issue.getProjectId()).isEqualTo(1);
       assertThat(issue.getEpicIid()).isEqualTo(5);
       assertThat(issue.getLabels()).isEqualTo("bar,foo");

    }
}
