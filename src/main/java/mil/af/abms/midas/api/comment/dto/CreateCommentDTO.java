package mil.af.abms.midas.api.comment.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.comment.dto.validation.IsAssertionOrMeasureComment;
import mil.af.abms.midas.api.validation.CommentExists;

@Getter
@IsAssertionOrMeasureComment
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommentDTO implements Serializable {

    @CommentExists(allowNull = true)
    private Long parentId;

    private Long assertionId;

    private Long measureId;

    @NotBlank(message = "text must not be blank")
    private String text;

}
