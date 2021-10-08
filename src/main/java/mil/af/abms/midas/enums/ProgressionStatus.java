package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.ProgressionStatusDTO;

@AllArgsConstructor
@Getter
public enum ProgressionStatus {

    COMPLETED("COMPLETED", "Complete", "#0fcf50"),
    ON_TRACK("ON_TRACK", "On Track", "#5dade2"),
    AT_RISK("AT_RISK", "At Risk", "#ff9800"),
    BLOCKED("BLOCKED", "Blocked", "#e91e63"),
    NOT_STARTED("NOT_STARTED", "Not Started", "#969696");

    private final String name;
    private final String label;
    private final String color;

    public static Stream<ProgressionStatus> stream() {
        return Stream.of(ProgressionStatus.values());
    }

    public static List<ProgressionStatusDTO> toDTO() {
        return stream().map(v -> new ProgressionStatusDTO(v.name, v.label, v.color)).collect(Collectors.toList());
    }
}
