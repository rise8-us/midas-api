package mil.af.abms.midas.api.portfolio.dto;

import java.time.LocalDate;
import java.util.Set;

import mil.af.abms.midas.api.dtos.AppGroupDTO;

public interface PortfolioInterfaceDTO extends AppGroupDTO {
    Set<Long> getCapabilityIds();
    Set<Long> getProductIds();
    String getDescription();
    String getVision();
    String getMission();
    String getProblemStatement();
    LocalDate getSprintStartDate();
    Integer getSprintDurationInDays();
}
