package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.EnumDTO;

@AllArgsConstructor
@Getter
public enum CompletionType {

    CONNECTION_FAILURE(
            "CONNECTION_FAILURE",
            "Connection Failure",
            "Connection to external source lost"
    ),
    BINARY(
            "BINARY",
            "Binary",
            "Complete? true or false"
    ),
    PERCENTAGE(
            "PERCENTAGE",
            "Percentage",
            "Percentage of completeness"
    ),
    NUMBER(
            "NUMBER",
            "Number",
            "Numerical representation of completeness"
    ),
    MONEY(
            "MONEY",
            "Money",
            "Monetary representation of completeness"
    ),
    GITLAB_EPIC(
            "GITLAB_EPIC",
            "GitLab Epic",
            "Progress synced to a GitLab epic"
    ),
    GITLAB_ISSUE(
            "GITLAB_ISSUE",
            "GitLab Issue",
            "Progress synced to a GitLab issue"
    );

    private final String name;
    private final String displayName;
    private final String description;

    public static Stream<CompletionType> stream() {
        return Stream.of(CompletionType.values());
    }

    public static List<EnumDTO> toDTO() {
        return stream().map(c -> new EnumDTO(c.name, c.displayName, c.description)).collect(Collectors.toList());
    }
}
