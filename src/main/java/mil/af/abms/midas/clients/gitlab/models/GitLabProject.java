package mil.af.abms.midas.clients.gitlab.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitLabProject {

    @JsonProperty("id")
    private Integer gitlabProjectId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("created_at")
    private LocalDateTime gitlabCreationDate;
    @JsonProperty("web_url")
    private String webUrl;
    @JsonProperty("avatar_url")
    private String avatarUrl;

}
