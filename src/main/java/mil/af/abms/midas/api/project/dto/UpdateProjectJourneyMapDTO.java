package mil.af.abms.midas.api.project.dto;

import javax.validation.constraints.NotNull;

import java.io.Serializable;

import lombok.Data;

@Data
public class UpdateProjectJourneyMapDTO implements Serializable {
    @NotNull
    private Long projectJourneyMap;
}
