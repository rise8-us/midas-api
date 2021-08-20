package mil.af.abms.midas.api.feature.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeatureDTO implements Serializable {

    @NotBlank(message = "Please enter a feature title")
    private String title;
    private String description;
    private Integer index;
    private Long id;
}
