package mil.af.abms.midas.api.metrics.dtos;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniqueRoleMetricsDTO implements AbstractDTO {

    private Set<Long> admins;
    private Set<Long> portfolioLeads;
    private Set<Long> productManagers;
    private Set<Long> techLeads;
    private Set<Long> designers;
    private Set<Long> platformOperators;
    private Set<Long> portfolioAdmins;
    private Set<Long> stakeholders;
    private Set<Long> unassigned;

}
