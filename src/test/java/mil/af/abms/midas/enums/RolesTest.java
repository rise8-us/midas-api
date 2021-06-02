package mil.af.abms.midas.enums;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

public class RolesTest {

    @Test
    public void can_Stream_Enum() {
        assertThat((int) Roles.stream().count()).isEqualTo(5);
    }

    @Test
    public void can_get_permission_by_long() {
        Map<Roles, Boolean> rolesMap = new HashMap<Roles, Boolean>();
        rolesMap.put(Roles.ADMIN, true);
        rolesMap.put(Roles.PORTFOLIO_LEAD, false);
        rolesMap.put(Roles.PRODUCT_MANAGER, false);
        rolesMap.put(Roles.TECH_LEAD, false);
        rolesMap.put(Roles.DESIGNER, false);

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
        assertThat(Roles.ADMIN.getTitle()).isEqualTo("Admin");
    }

    @Test
    public void should_return_expected_enum_add() {
        assertThat(Roles.PORTFOLIO_LEAD.getOffset()).isEqualTo(1);
        assertThat(Roles.PORTFOLIO_LEAD.getName()).isEqualTo("PORTFOLIO_LEAD");
        assertThat(Roles.PORTFOLIO_LEAD.getDescription()).isEqualTo("Manages portfolio");
        assertThat(Roles.PORTFOLIO_LEAD.getBitValue()).isEqualTo(2);
    }
}
