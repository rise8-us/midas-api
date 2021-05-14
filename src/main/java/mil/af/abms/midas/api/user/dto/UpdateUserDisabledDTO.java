package mil.af.abms.midas.api.user.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class UpdateUserDisabledDTO implements Serializable {
    private boolean isDisabled;
}
