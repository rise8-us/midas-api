package mil.af.abms.midas.api.assertion.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.AssertionExists;
import mil.af.abms.midas.api.validation.ObjectiveExists;
import mil.af.abms.midas.api.validation.TagsExist;
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

    @TagsExist
    private Set<Long> tagIds;

    @AssertionExists
    private Long parentId;
    private Set<Long> childIds;

    private String linkKey;

}
