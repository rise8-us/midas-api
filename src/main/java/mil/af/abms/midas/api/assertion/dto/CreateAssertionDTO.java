package mil.af.abms.midas.api.assertion.dto;

import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.dtos.CompletableDTO;
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.validation.AssertionExists;
import mil.af.abms.midas.api.validation.IsValidDueDate;
import mil.af.abms.midas.api.validation.ProductExists;
import mil.af.abms.midas.enums.ProgressionStatus;

@Data
@IsValidDueDate
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssertionDTO implements CompletableDTO {

    @NotBlank(message = "text must not be blank")
    private String text;

    @ProductExists(allowNull = false)
    private Long productId;

    @AssertionExists
    private Long parentId;

    @AssertionExists
    private Long inheritedFromId;

    @Transient
    private List<CreateMeasureDTO> measures;

    private ProgressionStatus status;
    private List<CreateAssertionDTO> children;
    private Long assignedPersonId;
    private Boolean isArchived =  false;

    private String startDate;
    private String dueDate;
}
