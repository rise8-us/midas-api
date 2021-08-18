package mil.af.abms.midas.api.feature.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateFeatureDTO implements Serializable {

    private String title;
    private String description;
    private Long productId;
    private Integer index;
}
