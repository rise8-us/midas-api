package mil.af.abms.midas.clients.gitlab.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpicConversion {

    private Integer epicIid;
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate startDateFromInheritedSource;
    private LocalDate dueDate;
    private LocalDate dueDateFromInheritedSource;
    private LocalDateTime closedAt;
    private String state;
    private String webUrl;
    private String selfApi;
    private String epicIssuesApi;

    public EpicConversion(){}

    @JsonCreator
    public EpicConversion(
            @JsonProperty("iid") Integer epicId,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("state") String state,
            @JsonProperty("web_url") String webUrl,
            @JsonProperty("start_date") LocalDate startDate,
            @JsonProperty("due_date") LocalDate dueDate,
            @JsonProperty("start_date_from_inherited_source") LocalDate startDateFromInheritedSource,
            @JsonProperty("due_date_from_inherited_source") LocalDate dueDateFromInheritedSource,
            @JsonProperty("closed_at") LocalDateTime closedAt
    ) {
        this.epicIid = epicId;
        this.title = title;
        this.description = description;
        this.state = state;
        this.webUrl = webUrl;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.startDateFromInheritedSource = startDateFromInheritedSource;
        this.dueDateFromInheritedSource = dueDateFromInheritedSource;
        this.closedAt = closedAt;
    }

    @JsonProperty("_links")
    private void unpackNestedLinks(Map<String,Object> links) {
        this.selfApi = (String)links.get("self");
        this.epicIssuesApi = (String)links.get("epic_issues");
    }

}
