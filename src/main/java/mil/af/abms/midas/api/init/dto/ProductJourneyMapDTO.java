package mil.af.abms.midas.api.init.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductJourneyMapDTO {

    private final Integer offset;
    private final String name;
    private final String description;

}
