package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AssertionType {

    OBJECTIVE(
            "Objective",
            "Defining an over-arching breakthrough vision",
            "Stable, concise and linked to company mission"
    ),
    GOAL(
            "Goal",
            "Stepping stones to achieving the higher level objective",
            "Specific, Measurable, Achievable, Compatible"
    ),
    STRATEGY(
            "Strategy",
            "The choices made to achieve an objective",
            "Where to focus, should be flexible"
    ),
    MEASURE(
            "Measure",
            "Numerical benchmarks on our progress",
            "KPIs used as checkpoints to determine if strategies are working"
    );

    private final String DisplayName;
    private final String description;
    private final String detail;
}