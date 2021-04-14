package mil.af.abms.midas.api.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO implements AbstractDTO {

    private Long id;
    private String label;
    private String description;
    private String color;

}
