package mil.af.abms.midas.api.capability.dto;

import java.io.Serializable;

public interface CapabilityInterfaceDTO extends Serializable {

    public String getDescription();
    public Integer getReferenceId();
    public Long getPortfolioId();
}
