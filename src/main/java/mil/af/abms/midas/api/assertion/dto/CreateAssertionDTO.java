package mil.af.abms.midas.api.assertion.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.AssertionExists;
import mil.af.abms.midas.api.validation.ProductExists;
import mil.af.abms.midas.enums.AssertionStatus;
import mil.af.abms.midas.enums.AssertionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAssertionDTO implements Serializable {

    @NotBlank(message = "text must not be blank")
    private String text;

    @NotNull(message = "type must not be blank")
    private AssertionType type;

    @ProductExists(allowNull = false)
    private Long productId;

    @AssertionExists
    private Long parentId;

    private AssertionStatus status;

    private List<CreateAssertionDTO> children;

}
