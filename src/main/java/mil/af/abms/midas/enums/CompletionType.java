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

    STRING(
            "STRING",
            "String",
            "manual text entry"
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
    );

    private final String name;
    private final String displayName;
    private final String description;

    public static Stream<CompletionType> stream() {
        return Stream.of(CompletionType.values());
    }

    public static List<CompletionTypeDTO> toDTO() {
        return stream().map(c -> new CompletionTypeDTO(c.name, c.displayName, c.description)).collect(Collectors.toList());
    }
}
