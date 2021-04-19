package mil.af.abms.midas.api.portfolio.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDTO implements AbstractDTO {

    private Long id;
    private String name;
    private Long portfolioManagerId;
    private String description;
    private Set<Long> productIds;
    private Boolean isArchived;
    private LocalDateTime creationDate;

}
