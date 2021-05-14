package mil.af.abms.midas.api.team.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UpdateTeamIsArchivedDTO implements Serializable {
    private Boolean isArchived;
}
