package mil.af.abms.midas.clients.gitlab.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.JsonMapper;
import mil.af.abms.midas.clients.gitlab.models.EpicConversion;

class EpicConversionTests {

    @Test
    void can_map_from_JSON() throws IOException {
        String resourceName = "src/test/resources/gitlab/epic.json";
        String conditionStr = Files.readString(Path.of(resourceName));
        InputStream stream = new ByteArrayInputStream(conditionStr.getBytes(StandardCharsets.UTF_8));

        EpicConversion epic = JsonMapper.dateMapper()
                .readerFor(EpicConversion.class)
                .readValue(stream);

       assertThat(epic.getEpicIid()).isEqualTo(4);
       assertThat(epic.getTitle()).isEqualTo("Accusamus");
       assertThat(epic.getDescription()).isEqualTo("Molestias");
       assertThat(epic.getState()).isEqualTo("opened");
       assertThat(epic.getWebUrl()).isEqualTo("http://gitlab.example.com/groups/test/-/epics/4");
       assertThat(epic.getSelfApi()).isEqualTo("http://gitlab.example.com/api/v4/groups/7/epics/4");
       assertThat(epic.getEpicIssuesApi()).isEqualTo("http://gitlab.example.com/api/v4/groups/7/epics/4/issues");
       assertThat(epic.getStartDate()).isEqualTo(LocalDate.parse("2018-07-01"));
       assertThat(epic.getDueDate()).isEqualTo(LocalDate.parse("2018-07-31"));
       assertThat(epic.getStartDateFromInheritedSource()).isEqualTo(LocalDate.parse("2018-07-01"));
       assertThat(epic.getDueDateFromInheritedSource()).isEqualTo(LocalDate.parse("2018-07-31"));
       assertThat(epic.getClosedAt().toString()).isEqualTo("2018-08-18T12:22:05.239");

    }
}
