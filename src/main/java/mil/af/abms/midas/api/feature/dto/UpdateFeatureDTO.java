package mil.af.abms.midas.api.feature.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateFeatureDTO implements Serializable {

    private String title;
    private String description;
    private Integer index;
    private Long id;
}
