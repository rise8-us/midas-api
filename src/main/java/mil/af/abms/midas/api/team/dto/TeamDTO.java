package mil.af.abms.midas.api.team.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamDTO implements AbstractDTO {

    private Long id;
    private LocalDateTime creationDate;
    private String name;
    private String description;
    private Boolean isArchived;
    private Set<Long> personnelIds;
    private Long productManagerId;
    private Long designerId;
    private Long techLeadId;
    private Set<Long> userIds;

}
