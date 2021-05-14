package mil.af.abms.midas.enums;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

import mil.af.abms.midas.api.init.dto.RoleDTO;

@Getter
@AllArgsConstructor
public enum Roles {
    ADMIN(0, "ADMIN", "Can update or add anything", "Admin"),
    PORTFOLIO_LEAD(1, "PORTFOLIO_LEAD", "Manages portfolio", "Portfolio Lead"),
    PRODUCT_MANAGER(2, "PRODUCT_MANAGER", "Manages products", "Product Manager"),
    TECH_LEAD(3, "TECH_LEAD", "Lead SWE in charge of technical functionality", "Tech Lead"),
    DESIGNER(4, "DESIGNER", "Manages product UI/UX designs", "Designer (UI/UX)");


    private final Integer offset;
    private final String name;
    private final String description;
    private final String title;

    public static Stream<Roles> stream() {
        return Stream.of(Roles.values());
    }

    public static Map<Roles, Boolean> getRoles(long rolesLong) {
        Map<Roles, Boolean> rolesMap = new EnumMap<>(Roles.class);
        Roles.stream().forEach(p -> rolesMap.put(p, (rolesLong & p.getBitValue()) > 0));
        return rolesMap;
    }

    public static Long setRoles(Long currentLong, Map<Roles, Boolean> updatedRolesMap) {
        Map<Roles, Boolean> currentRolesMap = getRoles(currentLong);
        updatedRolesMap.forEach(currentRolesMap::replace);
        return Roles.stream().filter(currentRolesMap::get).mapToLong(Roles::getBitValue).sum();
    }

    public static List<RoleDTO> toDTO() {
        return stream().map(v -> new RoleDTO(v.offset, v.name, v.description)).collect(Collectors.toList());
    }

    public Long getBitValue() {
        return Math.round(Math.pow(2, offset));
    }
}
