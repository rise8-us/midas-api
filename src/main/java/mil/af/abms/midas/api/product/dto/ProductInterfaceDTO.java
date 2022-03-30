package mil.af.abms.midas.api.product.dto;

import java.io.Serializable;
import java.util.Set;

public interface ProductInterfaceDTO extends Serializable {
    public Set<Long> getTagIds();
    public Set<Long> getProjectIds();
}
