package mil.af.abms.midas.api.ogsm.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.validation.ProductExists;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateOgsmDTO {

    @ProductExists (allowNull = true)
    private Long productId;

    private Set<CreateAssertionDTO> assertionDTOs;

}
