package mil.af.abms.midas.api.assertion.dto;

import javax.validation.constraints.NotBlank;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.enums.AssertionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssertionDTO {

    @NotBlank(message = "text must not be blank")
    private String text;
    private AssertionType type;
    private Set<Long> tagIds;
    private Set<Long> commentIds;

}
