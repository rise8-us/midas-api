package mil.af.abms.midas.api.feature.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateFeatureDTO implements Serializable {

    @NotBlank(message = "Please enter a feature title")
    private String title;
    private String description;
    private Long productId;
    private Integer index;
}
