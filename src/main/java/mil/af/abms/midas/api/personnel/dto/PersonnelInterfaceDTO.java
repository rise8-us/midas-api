package mil.af.abms.midas.api.personnel.dto;

import java.io.Serializable;
import java.util.Set;

public interface PersonnelInterfaceDTO extends Serializable {
    public Long getOwnerId();
    public Set<Long> getTeamIds();
    public Set<Long> getAdminIds();
}
