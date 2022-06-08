package mil.af.abms.midas.api.capability.dto;

import java.io.Serializable;

public interface CapabilityInterfaceDTO extends Serializable {

    String getDescription();
    Integer getReferenceId();
    Long getPortfolioId();
}
