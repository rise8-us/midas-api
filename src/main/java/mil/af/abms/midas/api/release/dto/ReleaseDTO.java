package mil.af.abms.midas.api.release.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReleaseDTO implements AbstractDTO {

    private Long id;
    private String name;
    private String description;
    private String tagName;
    private LocalDateTime releasedAt;

}
