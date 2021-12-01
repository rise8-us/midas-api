package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.RoadmapTypeDTO;

@AllArgsConstructor
@Getter
public enum RoadmapType {
    GITLAB(
            "GITLAB",
            "GitLab Epics",
            "All GitLab epics assigned to the group"
    ),
    MANUAL(
            "MANUAL",
            "Manual",
            "Manual entry of roadmap not tied to any source control"
    );

    private final String name;
    private final String displayName;
    private final String description;

    public static Stream<RoadmapType> stream() {
        return Stream.of(RoadmapType.values());
    }

    public static List<RoadmapTypeDTO> toDTO() {
        return stream().map(r -> new RoadmapTypeDTO(r.name, r.displayName, r.description)).collect(Collectors.toList());
    }
}
