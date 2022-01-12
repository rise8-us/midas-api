package mil.af.abms.midas.enums;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.EnumDTO;

@AllArgsConstructor
@Getter
public enum SonarqubeSecurity {

    U("U", "No Data"),
    A("A", "A is when there are no Vulnerabilities"),
    B("B", "B is when there is at least 1 Minor Vulnerability"),
    C("C", "C is when there is at least 1 Major Vulnerability"),
    D("D", "D is when there is at least 1 Critical Vulnerability"),
    E("E", "E is when there is at at least 1 Blocker Vulnerability");

    private final String name;
    private final String description;

    public static Stream<SonarqubeSecurity> stream() {
        return Stream.of(SonarqubeSecurity.values());
    }

    public static List<EnumDTO> toDTO() {
        return stream().map(s -> new EnumDTO(s.name, "Security", s.description)).collect(Collectors.toList());
    }
}
