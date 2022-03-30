package mil.af.abms.midas.api.portfolio.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.validation.UniquePortfolioName;

@Getter @Setter
public class UpdatePortfolioDTO implements Serializable {

    @NotBlank(message = "name must not be blank")
    @UniquePortfolioName(isNew = false)
    private String name;

    private String description;

    private Integer gitlabGroupId;
    private Long sourceControlId;
    private String vision;
    private String mission;
    private String problemStatement;

    private Set<Long> productIds;
    private UpdatePersonnelDTO personnel;
}
