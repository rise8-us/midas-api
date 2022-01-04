package mil.af.abms.midas.api.assertion.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.dtos.CompletableDTO;
import mil.af.abms.midas.api.validation.IsValidDueDate;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@IsValidDueDate
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssertionDTO implements CompletableDTO {

    @NotBlank(message = "text must not be blank")
    private String text;

    @NotNull(message = "A status must be provided")
    private ProgressionStatus status;

    private List<CreateAssertionDTO> children;

    private Long assignedPersonId;
    private Boolean isArchived = false;

    private String startDate;
    private String dueDate;

}
