package mil.af.abms.midas.api.sourcecontrol.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUpdateSourceControlDTO implements AbstractDTO {

    @NotBlank
    private String name;

    private String description;

    @NotBlank
    private String baseUrl;

    private String token;

}
