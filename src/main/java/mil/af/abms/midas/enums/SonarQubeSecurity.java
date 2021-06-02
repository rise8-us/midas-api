package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SonarQubeSecurity {

    U("U", "No Data"),
    A("A", "A is when there are no Vulnerabilities"),
    B("B", "B is when there is at least 1 Minor Vulnerability"),
    C("C", "C is when there is at least 1 Major Vulnerability"),
    D("D", "D is when there is at least 1 Critical Vulnerability"),
    E("E", "E is when there is at at least 1 Blocker Vulnerability");

    private final String name;
    private final String description;
}
