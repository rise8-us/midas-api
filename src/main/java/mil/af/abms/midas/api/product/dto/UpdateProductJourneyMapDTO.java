package mil.af.abms.midas.api.product.dto;

import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class UpdateProductJourneyMapDTO {
    @NotNull
    private Long productJourneyMap;
}
