package mil.af.abms.midas.api.product.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UpdateProductIsArchivedDTO implements Serializable {
    private Boolean isArchived;
}
