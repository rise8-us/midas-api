package mil.af.abms.midas.api.problem.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProblemDTO implements AbstractDTO {

    private Long id;
    private Long createdById;
    private Long productId;
    private String text;
    private Boolean isCurrent;
    private LocalDateTime creationDate;

}
