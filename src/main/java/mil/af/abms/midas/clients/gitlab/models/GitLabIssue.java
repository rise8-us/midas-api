package mil.af.abms.midas.clients.gitlab.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabIssue {

    @JsonProperty("title")
    private String title;
    @JsonProperty("description")
    private String description;
    @JsonProperty("created_at")
    private LocalDateTime creationDate;
    @JsonProperty("due_date")
    private LocalDate dueDate;
    @JsonProperty("closed_at")
    private LocalDateTime completedAt;
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
    @JsonProperty("iid")
    private Integer issueIid;
    @JsonProperty("state")
    private String state;
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("weight")
    private Long weight;
    @JsonProperty("project_id")
    private Long projectId;

    private Integer epicIid;

    @JsonProperty("epic")
    private void unpackNestedEpic(Map<String, Object> epic) {
        this.epicIid = (Integer) epic.get("iid");
    }

}
