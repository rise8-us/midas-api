package mil.af.abms.midas.enums;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Roles {
    ADMIN(0, "ADMIN", "Can update or add anything"),
    PLACEHOLDER(1, "PLACEHOLDER", "Placeholder role");

    private final Integer offset;
    private final String name;
    private final String description;

    public static Stream<Roles> stream() {
        return Stream.of(Roles.values());
    }

    public static Map<Roles, Boolean> getRoles(long rolesLong) {
        Map<Roles, Boolean> rolesMap = new EnumMap<>(Roles.class);

        Roles.stream().forEach(p -> {
            rolesMap.put(p, (rolesLong & p.getBitValue()) > 0);
        });
        return rolesMap;
    }

    public static Long setRoles(Long currentLong, Map<Roles, Boolean> updatedRolesMap) {
        Map<Roles, Boolean> currentRolesMap = getRoles(currentLong);

        updatedRolesMap.forEach(currentRolesMap::replace);
        return Roles.stream().filter(currentRolesMap::get).mapToLong(Roles::getBitValue).sum();
    }

    public Long getBitValue() {
        return Math.round(Math.pow(2, offset));
    }
}
