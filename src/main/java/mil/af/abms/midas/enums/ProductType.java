package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductType {

    APPLICATION("APPLICATION", "Application", "A collection of projects such as a api and ui"),
    PORTFOLIO("PORTFOLIO", "Portfolio", "A collection of applications");

    private final String name;
    private final String label;
    private final String description;

}
