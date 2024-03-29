package mil.af.abms.midas.api.sourcecontrol.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SourceControlDTO implements AbstractDTO {

    private Long id;
    private String name;
    private String description;
    private String baseUrl;
    private LocalDateTime creationDate;

}
