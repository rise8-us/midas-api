package mil.af.abms.midas.api.objective.dto;

import javax.validation.constraints.NotEmpty;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.validation.ProductExists;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateObjectiveDTO {

    @ProductExists (allowNull = true)
    private Long productId;

    @NotEmpty(message = "Objective cannot be blank")
    private String text;

    private Set<CreateAssertionDTO> assertionDTOs;

}
