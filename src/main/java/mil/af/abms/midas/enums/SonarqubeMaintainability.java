package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.EnumDTO;

@AllArgsConstructor
@Getter
public enum SonarqubeMaintainability {

    U("U", "No Data"),
    A("A", "A is when remediation cost is equal or less than 5%"),
    B("B", "B is when remediation cost is between 6 to 10%"),
    C("C", "C is when remediation cost is between 11 to 20%"),
    D("D", "D is when remediation cost is between 21 to 50%"),
    E("E", "E is when remediation cost is over 50%");

    private final String name;
    private final String description;

    public static Stream<SonarqubeMaintainability> stream() {
        return Stream.of(SonarqubeMaintainability.values());
    }

    public static List<EnumDTO> toDTO() {
        return stream().map(s -> new EnumDTO(s.name, "Maintainability", s.description)).collect(Collectors.toList());
    }
}
