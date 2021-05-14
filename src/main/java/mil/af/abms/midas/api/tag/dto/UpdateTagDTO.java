package mil.af.abms.midas.api.tag.dto;

import javax.validation.constraints.NotBlank;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.tag.validation.UniqueLabel;
import mil.af.abms.midas.api.validation.ValidHex;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateTagDTO implements Serializable {

    @NotBlank(message = "label must not be blank")
    @UniqueLabel(isNew = false)
    private String label;

    private String description;

    @NotBlank(message = "color must not be blank")
    @ValidHex
    private String color;

}
