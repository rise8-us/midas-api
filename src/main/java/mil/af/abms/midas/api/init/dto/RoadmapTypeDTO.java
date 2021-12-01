package mil.af.abms.midas.api.init.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoadmapTypeDTO implements Serializable {
    private final String name;
    private final String displayName;
    private final String description;
}
