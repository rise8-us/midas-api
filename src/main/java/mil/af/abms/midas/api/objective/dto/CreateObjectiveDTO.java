package mil.af.abms.midas.api.objective.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

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

    @ProductExists (allowNull = false)
    private Long productId;

    @NotEmpty(message = "Objective cannot be blank")
    private String text;

    @NotNull(message = "assertionDTOs cannot be null")
    private Set<CreateAssertionDTO> assertionDTOs;

}
