package mil.af.abms.midas.api.assertion.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArchiveAssertionDTO implements Serializable {
    private Boolean isArchived;
}
