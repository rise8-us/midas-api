package mil.af.abms.midas.api.comment.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.user.dto.UserDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO implements AbstractDTO {

    private Long id;
    private UserDTO author;
    private Long parentId;
    private String text;
    private Set<Long> children;
    private LocalDateTime creationDate;
    private LocalDateTime lastEdit;
    private Long editedById;

}
