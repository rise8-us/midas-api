package mil.af.abms.midas.api.tag.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.tag.validation.UniqueLabel;
import mil.af.abms.midas.api.tag.validation.ValidHex;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateTagDTO {

    @NotBlank(message = "label must not be blank")
    @UniqueLabel(isNew = true)
    private String label;
    private String description;
    @ValidHex
    private String color;

}
