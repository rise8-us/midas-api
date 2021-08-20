package mil.af.abms.midas.api.persona.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreatePersonaDTO implements Serializable {

    @NotBlank(message = "Please enter a persona title")
    private String title;
    private String description;
    private Long productId;
    private Boolean isSupported;
    private Integer index;
}
