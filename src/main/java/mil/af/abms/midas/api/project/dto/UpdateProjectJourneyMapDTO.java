package mil.af.abms.midas.api.project.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateProjectJourneyMapDTO {
    @NotNull
    private Long projectJourneyMap;
}
