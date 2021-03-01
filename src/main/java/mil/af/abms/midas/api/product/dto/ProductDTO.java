package mil.af.abms.midas.api.product.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements AbstractDTO {

    private Long id;
    private String name;
    private String description;
    private Boolean isArchived;
    private LocalDateTime creationDate;
    private Long gitlabProjectId;
    private Long teamId;

}
