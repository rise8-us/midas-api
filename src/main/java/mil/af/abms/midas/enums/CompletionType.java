package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum CompletionType {

    STRING(
            "String",
            "manual text entry"
    ),
    BINARY(
            "Binary",
            "Complete? true or false"
    ),
    PERCENTAGE(
            "Percentage",
            "Percentage of completeness"
    );

    private final String displayName;
    private final String description;
}
