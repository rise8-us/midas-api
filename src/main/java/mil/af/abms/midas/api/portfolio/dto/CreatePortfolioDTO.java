package mil.af.abms.midas.api.portfolio.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.Set;

import lombok.Data;

import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.validation.UniquePortfolioName;
import mil.af.abms.midas.api.validation.ProductsExist;

@Data
public class CreatePortfolioDTO implements Serializable {

    @NotBlank(message = "name must not be blank")
    @UniquePortfolioName(isNew = true)
    private String name;
    private String description;
    private CreatePersonnelDTO personnel;
    private Integer gitlabGroupId;
    private Long sourceControlId;
    private String vision;
    private String mission;
    private String problemStatement;

    @ProductsExist
    private Set<Long> productIds;
}