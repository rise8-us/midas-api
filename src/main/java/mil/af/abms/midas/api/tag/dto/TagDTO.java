package mil.af.abms.midas.api.tag.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;
import mil.af.abms.midas.enums.TagType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagDTO implements AbstractDTO {

    private Long id;
    private String label;
    private String description;
    private String color;
    private Long createdById;
    private TagType tagType;

}
