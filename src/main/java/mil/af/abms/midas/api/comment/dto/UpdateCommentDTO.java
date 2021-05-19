package mil.af.abms.midas.api.comment.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCommentDTO implements Serializable {

    @NotBlank(message = "text must not be blank")
    private String text;

}
