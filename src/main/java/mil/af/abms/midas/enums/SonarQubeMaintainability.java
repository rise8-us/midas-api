package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SonarQubeMaintainability {

    U("U", "No Data"),
    A("A", "A is when remediation cost is equal or less than 5%"),
    B("B", "B is when remediation cost is between 6 to 10%"),
    C("C", "C is when remediation cost is between 11 to 20%"),
    D("D", "D is when remediation cost is between 21 to 50%"),
    E("E", "E is when remediation cost is over 50%");

    private final String name;
    private final String description;
}
