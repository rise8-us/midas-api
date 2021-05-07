package mil.af.abms.midas.api.assertion.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.AssertionExists;
import mil.af.abms.midas.api.validation.ObjectiveExists;
import mil.af.abms.midas.enums.AssertionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssertionDTO {

    @NotBlank(message = "text must not be blank")
    private String text;

    @NotNull(message = "type must not be blank")
    private AssertionType type;

    @ObjectiveExists
    private Long objectiveId;

    @AssertionExists(allowNull = true)
    private Long parentId;

    private String linkKey;

}
