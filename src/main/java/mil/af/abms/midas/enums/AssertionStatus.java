package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.AssertionStatusDTO;

@AllArgsConstructor
@Getter
public enum AssertionStatus {

    NOT_STARTED("NOT_STARTED", "Not Started", "#969696"),
    ON_TRACK("ON_TRACK", "On Track", "#8bc34a"),
    BLOCKED("BLOCKED", "Blocked", "#e91e63"),
    AT_RISK("AT_RISK", "At Risk", "#ff9800"),
    COMPLETED("COMPLETED", "Completed", "#0fcf50");

    private final String name;
    private final String label;
    private final String color;

    public static Stream<AssertionStatus> stream() {
        return Stream.of(AssertionStatus.values());
    }

    public static List<AssertionStatusDTO> toDTO() {
        return stream().map(v -> new AssertionStatusDTO(v.name, v.label, v.color)).collect(Collectors.toList());
    }
}
