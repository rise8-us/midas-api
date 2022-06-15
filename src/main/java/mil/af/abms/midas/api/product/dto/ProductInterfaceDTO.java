package mil.af.abms.midas.api.product.dto;

import java.util.Set;

import mil.af.abms.midas.api.dtos.AppGroupDTO;

public interface ProductInterfaceDTO extends AppGroupDTO {
    Set<Long> getTagIds();
    Set<Long> getProjectIds();
}
