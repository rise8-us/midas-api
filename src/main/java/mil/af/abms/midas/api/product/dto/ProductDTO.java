package mil.af.abms.midas.api.product.dto;

import java.time.LocalDateTime;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO implements AbstractDTO {

    private Long id;
    private Long productManagerId;
    private Long portfolioId;
    private String name;
    private String description;
    private String visionStatement;
    private Boolean isArchived;
    private LocalDateTime creationDate;
    private Set<Long> projectIds;
    private Set<Long> tagIds;

}
