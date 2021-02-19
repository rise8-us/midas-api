package us.rise8.mixer.api.init.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RoleDTO {
    private final String name;
    private final Integer offset;
    private final String description;
}
