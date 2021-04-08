package mil.af.abms.midas.api.portfolio.dto;

import javax.validation.constraints.NotBlank;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.portfolio.validation.UniqueName;
import mil.af.abms.midas.api.portfolio.validation.UserExists;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePortfolioDTO {

    @NotBlank(message = "Portfolio name must not be blank")
    @UniqueName(isNew = true)
    private String name;
    @UserExists
    private Long leadId;
    private String description;
    private Set<Long> projectsIds;
    private Boolean isArchived;

}
