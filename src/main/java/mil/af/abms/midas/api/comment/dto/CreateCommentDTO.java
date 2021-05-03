package mil.af.abms.midas.api.comment.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.validation.AssertionExists;
import mil.af.abms.midas.api.validation.CommentExists;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDTO {

    @CommentExists(allowNull = true)
    private Long parentId;

    @AssertionExists
    private Long assertionId;

    @NotBlank(message = "text must not be blank")
    private String text;

}
