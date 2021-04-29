package mil.af.abms.midas.api.assertion.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.AssertionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssertionDTO implements AbstractDTO {

    private Long id;
    private Long ogsmId;
    private Long createdById;
    private String text;
    private AssertionType type;
    private LocalDateTime creationDate;
    private Set<Long> tagIds;
    private Set<Long> commentIds;

}
