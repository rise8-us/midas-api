package mil.af.abms.midas.api.dtos;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import mil.af.abms.midas.api.AbstractDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IsHiddenDTO implements AbstractDTO {

    @NotNull
    private Boolean isHidden;

}
