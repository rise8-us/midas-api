package mil.af.abms.midas.api.portfolio.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Set;

public interface PortfolioInterfaceDTO extends Serializable {
    Set<Long> getCapabilityIds();
    Set<Long> getProductIds();
    String getDescription();
    String getVision();
    String getMission();
    String getProblemStatement();
    Integer getGitlabGroupId();
    Long getSourceControlId();
    LocalDate getSprintStartDate();
    Integer getSprintDurationInDays();
}
