package mil.af.abms.midas.api;

import java.util.Set;

import mil.af.abms.midas.api.deliverable.Deliverable;

public interface DeliverableInterface {

    Set<Deliverable> getDeliverables();
    void setDeliverables(Set<Deliverable> comments);
    AbstractDTO toDto();
    String getLowercaseClassName();
}
