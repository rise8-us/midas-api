package mil.af.abms.midas.api.init.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import mil.af.abms.midas.enums.Roles;

@Getter
public class InfoDTO {
    public InfoDTO(String classificationString, String caveat) {
        List<RoleDTO> rolesListDTO = new ArrayList<>();
        Roles.stream().forEach(r -> rolesListDTO.add(
                new RoleDTO(r.getName(), r.getOffset(), r.getDescription())
        ));
        this.roles = rolesListDTO;

        this.classification = new ClassificationDTO(classificationString, caveat);

    }

    private final ClassificationDTO classification;
    private final List<RoleDTO> roles;
}
