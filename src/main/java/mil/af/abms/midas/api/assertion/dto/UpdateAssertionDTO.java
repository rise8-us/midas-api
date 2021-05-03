package mil.af.abms.midas.api.assertion.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.AssertionExists;
import mil.af.abms.midas.api.validation.TagsExist;
import mil.af.abms.midas.enums.AssertionType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAssertionDTO {

    @NotBlank(message = "text must not be blank")
    private String text;

    @NotNull(message = "type must not be blank")
    private AssertionType type;

    @TagsExist
    private Set<Long> tagIds;

    private Set<Long> commentIds;

    @AssertionExists
    private Long parentId;
    private Set<Long> childIds;

}
