package mil.af.abms.midas.enums;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.init.dto.BitwiseDTO;

public class RolesTest {

    @Test
    public void can_Stream_Enum() {
        assertThat((int) Roles.stream().count()).isEqualTo(9);
    }

    @Test
    public void can_get_permission_by_long() {
        Map<Roles, Boolean> rolesMap = new HashMap<Roles, Boolean>();
        rolesMap.put(Roles.ADMIN, true);
        rolesMap.put(Roles.PORTFOLIO_LEAD, false);
        rolesMap.put(Roles.PRODUCT_MANAGER, false);
        rolesMap.put(Roles.TECH_LEAD, false);
        rolesMap.put(Roles.DESIGNER, false);
        rolesMap.put(Roles.PLATFORM_OPERATOR, false);
        rolesMap.put(Roles.PORTFOLIO_ADMIN, false);
        rolesMap.put(Roles.STAKEHOLDER, false);
        rolesMap.put(Roles.TESTER, false);

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

    @Test
    public void should_convert_to_dto() {
        var bitWiseDTO = List.of(new BitwiseDTO(0, "ADMIN", "Can update or add anything"));

        assertThat(Roles.toDTO().get(0).getOffset()).isEqualTo(bitWiseDTO.get(0).getOffset());
        assertThat(Roles.toDTO().get(0).getName()).isEqualTo(bitWiseDTO.get(0).getName());
        assertThat(Roles.toDTO().get(0).getDescription()).isEqualTo(bitWiseDTO.get(0).getDescription());
    }
}
