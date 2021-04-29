package mil.af.abms.midas.api.comment.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO implements AbstractDTO {

    private Long id;
    private Long createdById;
    private Long parentId;
    private Long assertionId;
    private String text;
    private Set<Long> children;
    private LocalDateTime creationDate;

}
