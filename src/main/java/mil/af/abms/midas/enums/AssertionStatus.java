package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AssertionStatus {

    NOT_STARTED("NOT_STARTED","Not Started"),
    STARTED("STARTED","Started"),
    ON_TRACK("ON_TRACK", "On Track"),
    NEEDS_ATTENTION("NEEDS_ATTENTION", "Needs Attention"),
    AT_RISK("AT_RISK","At Risk"),
    COMPLETED("COMPLETED", "Completed");

    private final String name;
    private final String displayName;
}
