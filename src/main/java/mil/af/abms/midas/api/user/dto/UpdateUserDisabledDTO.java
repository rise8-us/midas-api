package mil.af.abms.midas.api.user.dto;

import java.io.Serializable;

import lombok.Data;

/**
 * @deprecated (superseded by userType, will be removed in future version)
 */
@Deprecated
@Data
public class UpdateUserDisabledDTO implements Serializable {
    private boolean isDisabled;
}
