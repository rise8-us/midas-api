package mil.af.abms.midas.api.comment.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentDTO {

    private Long parentId;
    private Long assertionId;

    @NotBlank(message = "text must not be blank")
    private String text;

}
