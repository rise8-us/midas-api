package mil.af.abms.midas.api.personnel.dto;

import java.io.Serializable;
import java.util.Set;

public interface PersonnelInterfaceDTO extends Serializable {
    Long getOwnerId();
    Set<Long> getTeamIds();
    Set<Long> getAdminIds();
}
