package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SonarQubeReliability {

    U("U", "No Data"),
    A("A", "A is when there are no bugs"),
    B("B", "B is when there is at least 1 Minor Bug"),
    C("C", "C is when there is at least 1 Major Bug"),
    D("D", "D is when there is at least 1 Critical Bug"),
    E("E", "E is when there is at at least 1 Blocker Bug");

    private final String name;
    private final String description;
}
