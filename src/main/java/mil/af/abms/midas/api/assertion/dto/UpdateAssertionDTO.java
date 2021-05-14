package mil.af.abms.midas.api.assertion.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.AssertionStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssertionDTO implements Serializable {

    @NotBlank(message = "text must not be blank")
    private String text;

    @NotNull(message = "A status must be provided")
    private AssertionStatus status;

    private List<CreateAssertionDTO> children;

}
