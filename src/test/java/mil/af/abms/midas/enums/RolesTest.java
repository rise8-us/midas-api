package mil.af.abms.midas.enums;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class RolesTest {

    @Test
    public void canStreamEnum() {
        assertThat(Roles.stream().collect(Collectors.toList()).size()).isEqualTo(2);
    }

    @Test
    public void can_get_permission_by_long() {
        Map<Roles, Boolean> rolesMap = new HashMap<Roles, Boolean>();
        rolesMap.put(Roles.ADMIN, true);
        rolesMap.put(Roles.PLACEHOLDER, false);

        assertThat(Roles.getRoles(1L)).isEqualTo(rolesMap);
    }

    @Test
    public void can_update_permission_map() {
        Map<Roles, Boolean> rolesMap = new HashMap<Roles, Boolean>();
        rolesMap.put(Roles.ADMIN, true);

        assertThat(Roles.setRoles(0L, rolesMap)).isEqualTo(1);
    }

    @Test
    public void should_return_expected_enum_admin() {
        assertThat(Roles.ADMIN.getOffset()).isEqualTo(0);
        assertThat(Roles.ADMIN.getName()).isEqualTo("ADMIN");
        assertThat(Roles.ADMIN.getDescription()).isEqualTo("Can update or add anything");
        assertThat(Roles.ADMIN.getBitValue()).isEqualTo(1);
    }

    @Test
    public void should_return_expected_enum_add() {
        assertThat(Roles.PLACEHOLDER.getOffset()).isEqualTo(1);
        assertThat(Roles.PLACEHOLDER.getName()).isEqualTo("PLACEHOLDER");
        assertThat(Roles.PLACEHOLDER.getDescription()).isEqualTo("Placeholder role");
        assertThat(Roles.PLACEHOLDER.getBitValue()).isEqualTo(2);
    }
}
