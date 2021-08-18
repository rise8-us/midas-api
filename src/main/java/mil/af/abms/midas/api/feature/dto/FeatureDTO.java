package mil.af.abms.midas.api.feature.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureDTO implements AbstractDTO {

    private Long id;
    private String title;
    private LocalDateTime creationDate;
    private String description;
    private Long productId;
    private Integer index;

}
