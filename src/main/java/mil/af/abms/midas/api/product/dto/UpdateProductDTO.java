package mil.af.abms.midas.api.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.product.validation.TeamExists;
import mil.af.abms.midas.api.product.validation.UniqueName;

@Getter
@AllArgsConstructor
public class UpdateProductDTO {

    @UniqueName(isNew = false)
    private final String name;
    @TeamExists
    private final Long teamId;
    private final String description;
    private final Boolean isArchived;
    private final Long gitlabProjectId;

}
