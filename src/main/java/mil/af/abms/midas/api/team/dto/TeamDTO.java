package mil.af.abms.midas.api.team.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO implements AbstractDTO {

    private Long id;
    private String name;
    private Boolean isArchived;
    private LocalDateTime creationDate;
    private Long GitlabGroupId;
    private String description;
}
