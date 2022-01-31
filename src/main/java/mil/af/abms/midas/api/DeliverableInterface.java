package mil.af.abms.midas.api;

import java.util.Set;

import mil.af.abms.midas.api.deliverable.Deliverable;

public interface DeliverableInterface {

    public Set<Deliverable> getDeliverables();
    public void setDeliverables(Set<Deliverable> comments);
    public AbstractDTO toDto();
    public String getLowercaseClassName();
}
