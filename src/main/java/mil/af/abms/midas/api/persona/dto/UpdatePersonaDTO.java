package mil.af.abms.midas.api.persona.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePersonaDTO implements Serializable {

    private String title;
    private String description;
    private Boolean isSupported;
    private Integer index;
    private Long id;
}
