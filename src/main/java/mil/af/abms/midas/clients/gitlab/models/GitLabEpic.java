package mil.af.abms.midas.clients.gitlab.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabEpic {

    @JsonProperty("iid")
    private Integer epicIid;
    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("start_date")
    private LocalDate startDate;
    @JsonProperty("start_date_from_inherited_source")
    private LocalDate startDateFromInheritedSource;
    @JsonProperty("due_date")
    private LocalDate dueDate;
    @JsonProperty("due_date_from_inherited_source")
    private LocalDate dueDateFromInheritedSource;
    @JsonProperty("closed_at")
    private LocalDateTime completedAt;
    @JsonProperty("state")
    private String state;
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("group_id")
    private Integer groupId;

}
