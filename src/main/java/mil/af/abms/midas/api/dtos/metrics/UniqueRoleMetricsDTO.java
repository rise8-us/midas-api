package mil.af.abms.midas.api.dtos.metrics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UniqueRoleMetricsDTO {

    private Object admins;
    private Object portfolioLeads;
    private Object productManagers;
    private Object techLeads;
    private Object designers;
    private Object platformOperators;
    private Object portfolioAdmins;
    private Object stakeholders;
    private Object unassigned;

}
