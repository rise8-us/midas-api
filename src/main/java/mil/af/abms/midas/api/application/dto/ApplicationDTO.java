package mil.af.abms.midas.api.application.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDTO implements AbstractDTO {

    private Long id;
    private String name;
    private Long productManagerId;
    private String description;
    private Set<Long> projectIds;
    private Boolean isArchived;
    private LocalDateTime creationDate;
    private Long portfolioId;
    private Set<Long> tagIds;

}
