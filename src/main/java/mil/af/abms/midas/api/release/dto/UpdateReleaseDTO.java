package mil.af.abms.midas.api.release.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateReleaseDTO implements Serializable {

    @NotBlank(message = "Release must have a title")
    @NotNull(message = "Release must have a title")
    private String title;

    @NotBlank(message = "Please enter a targeted completion date")
    private String targetDate;

    private ProgressionStatus status;

    private Set<Long> deliverableIds;
}
