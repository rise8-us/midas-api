package mil.af.abms.midas.api.project.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArchiveProjectDTO implements Serializable {
    private Boolean isArchived;
}
