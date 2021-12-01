package mil.af.abms.midas.api.assertion.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssertionDTO implements Serializable {

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
