package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.RoadmapStatusDTO;

@AllArgsConstructor
@Getter
public enum RoadmapStatus {

    FUTURE("FUTURE", "Future", "#797979"),
    IN_PROGRESS("IN_PROGRESS", "On Track", "#7FFFD4"),
    COMPLETE("COMPLETE", "Complete", "#8BC34A");

    private final String name;
    private final String label;
    private final String color;

    public static Stream<RoadmapStatus> stream() {
        return Stream.of(RoadmapStatus.values());
    }

    public static List<RoadmapStatusDTO> toDTO() {
        return stream().map(v -> new RoadmapStatusDTO(v.name, v.label, v.color)).collect(Collectors.toList());
    }
}
