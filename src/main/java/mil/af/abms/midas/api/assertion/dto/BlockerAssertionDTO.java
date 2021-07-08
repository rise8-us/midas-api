package mil.af.abms.midas.api.assertion.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.api.comment.dto.CommentDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlockerAssertionDTO implements AbstractDTO {

    private Long productParentId;
    private Long productId;
    private String productName;

    private AssertionDTO assertion;

    private CommentDTO comment;
}
