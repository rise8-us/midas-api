package mil.af.abms.midas.api.portfolio.dto;

import java.io.Serializable;
import java.util.Set;

public interface PortfolioInterfaceDTO extends Serializable {
    public Set<Long> getCapabilityIds();
    public Set<Long> getProductIds();
    public String getDescription();
    public String getVision();
    public String getMission();
    public String getProblemStatement();
    public Integer getGitlabGroupId();
    public Long getSourceControlId();
}
