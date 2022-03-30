package mil.af.abms.midas.api.personnel;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Set;

import org.springframework.beans.BeanUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.User;

public class PersonnelTests {

    private final Set<Team> teams = Set.of(Builder.build(Team.class).with(p -> p.setId(3L)).get());
    private final Set<User> admins = Set.of(Builder.build(User.class).with(p -> p.setId(4L)).get());
    private final User owner = Builder.build(User.class).with(u -> u.setId(2L)).get();
    private final Personnel personnel = Builder.build(Personnel.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setOwner(owner))
            .with(p -> p.setPortfolios(new Portfolio()))
            .with(p -> p.setProduct(new Product()))
            .with(p -> p.setTeams(teams))
            .with(p -> p.setAdmins(admins))
            .get();

    private final PersonnelDTO personnelDTO = Builder.build(PersonnelDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setOwnerId(2L))
            .with(d -> d.setAdminIds(Set.of(4L)))
            .with(d -> d.setTeamIds(Set.of(3L)))
            .get();

    @Test
    void should_have_all_dto_fields() {

        assertThat(4).isEqualTo(PersonnelDTO.class.getDeclaredFields().length);
        //This assertion is 4 because the DTO has less information than a Personnel Object
    }

    @Test
    void should_be_equal() {
        Personnel personnel2 = new Personnel();
        BeanUtils.copyProperties(personnel, personnel2);

        assertEquals(personnel, personnel);
        assertNotEquals(personnel, null);
        assertNotEquals(personnel, new User());
        assertNotEquals(personnel, new Personnel());
        assertEquals(personnel, personnel2);
    }

    @Test
    void should_get_properties() {
        assertThat(personnel.getId()).isEqualTo(1L);
        assertThat(personnel.getTeams()).isEqualTo(teams);
        assertThat(personnel.getAdmins()).isEqualTo(admins);
    }

    @Test
    void can_return_dto() {
        assertThat(personnel.toDto()).isEqualTo(personnelDTO);
    }
}
