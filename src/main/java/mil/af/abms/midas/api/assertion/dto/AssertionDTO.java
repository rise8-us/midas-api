package mil.af.abms.midas.api.assertion.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.AssertionStatus;
import mil.af.abms.midas.enums.AssertionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssertionDTO implements AbstractDTO {

    private Long id;
    private Long ogsmId;
    private Long createdById;
    private Long parentId;
    private String text;
    private AssertionType type;
    private LocalDateTime creationDate;
    private AssertionStatus status;
    private Set<Long> commentIds;
    private Set<Long> childIds;

}
