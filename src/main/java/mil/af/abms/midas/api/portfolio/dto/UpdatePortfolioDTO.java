package mil.af.abms.midas.api.portfolio.dto;

import javax.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;

import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.validation.UniquePortfolioName;
import mil.af.abms.midas.api.validation.CapabilitiesExist;
import mil.af.abms.midas.api.validation.ProductsExist;
import mil.af.abms.midas.api.validation.UniqueGroupIdAndSourceControl;

@Data
@UniqueGroupIdAndSourceControl
public class UpdatePortfolioDTO implements PortfolioInterfaceDTO {

    @NotBlank(message = "name must not be blank")
    @UniquePortfolioName(isNew = false)
    private String name;
    private String description;
    private Integer gitlabGroupId;
    private Long sourceControlId;
    private String vision;
    private String mission;
    private String problemStatement;
    private UpdatePersonnelDTO personnel;
    private LocalDate sprintStartDate;
    private Integer sprintDurationInDays;

    @ProductsExist
    private Set<Long> productIds;
    @CapabilitiesExist
    private Set<Long> capabilityIds;

}
