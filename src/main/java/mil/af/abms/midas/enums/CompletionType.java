package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.CompletionTypeDTO;

@AllArgsConstructor
@Getter
public enum CompletionType {

    CONNECTION_FAILURE(
            "CONNECTION_FAILURE",
            "Connection Failure",
            "Connection to external source lost",
            null
    ),
    BINARY(
            "BINARY",
            "Binary",
            "Complete? true or false",
            null
    ),
    PERCENTAGE(
            "PERCENTAGE",
            "Percentage",
            "Percentage of completeness",
            null
    ),
    NUMBER(
            "NUMBER",
            "Number",
            "Numerical representation of completeness",
            null
    ),
    MONEY(
            "MONEY",
            "Money",
            "Monetary representation of completeness",
            null
    ),
    GITLAB_EPIC(
            "GITLAB_EPIC",
            "GitLab Epic",
            "Synced to a GitLab epic",
            "total weight"
    ),
    GITLAB_ISSUE(
            "GITLAB_ISSUE",
            "GitLab Issue",
            "Synced to a GitLab issue",
            null
    );

    private final String name;
    private final String displayName;
    private final String description;
    private final String descriptor;

    public static Stream<CompletionType> stream() {
        return Stream.of(CompletionType.values());
    }

    public static List<CompletionTypeDTO> toDTO() {
        return stream().map(c -> new CompletionTypeDTO(c.name, c.displayName, c.description, c.descriptor)).collect(Collectors.toList());
    }
}
