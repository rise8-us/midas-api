package mil.af.abms.midas.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SprintSummaryPortfolioDTO {

    private Integer totalReleases;
    private Integer totalIssuesClosed;
    private Integer totalIssuesDelivered;

}
