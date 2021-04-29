package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProductType {

    APPLICATION("Application", "A collection of projects such as a api and ui"),
    PORTFOLIO("Portfolio", "A collection of applications");

    private final String DisplayName;
    private final String description;
}