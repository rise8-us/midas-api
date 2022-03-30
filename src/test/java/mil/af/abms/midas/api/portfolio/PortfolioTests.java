package mil.af.abms.midas.api.portfolio;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.dto.PersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.user.User;

public class PortfolioTests {

    private final PersonnelDTO personnelDTO = Builder.build(PersonnelDTO.class)
            .with(d -> d.setTeamIds(Set.of()))
            .with(d -> d.setAdminIds(Set.of()))
            .get();
    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setId(3L))
            .get();

    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("portfolio name"))
            .with(p -> p.setDescription("description"))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setGitlabGroupId(2))
            .with(p -> p.setPersonnel(new Personnel()))
            .with(p -> p.setSourceControl(sourceControl))
            .with(p -> p.setProducts(Set.of()))
            .with(p -> p.setVision("vision"))
            .with(p -> p.setMission("mission"))
            .with(p -> p.setProblemStatement("problem]"))
            .get();

    private final PortfolioDTO portfolioDTO = Builder.build(PortfolioDTO.class)
            .with(d -> d.setId(1L))
            .with(d -> d.setName("portfolio name"))
            .with(d -> d.setDescription("description"))
            .with(d -> d.setIsArchived(false))
            .with(d -> d.setCreationDate(portfolio.getCreationDate()))
            .with(d -> d.setProductIds(new HashSet<>()))
            .with(d -> d.setGitlabGroupId(2))
            .with(d -> d.setSourceControlId(3L))
            .with(d -> d.setPersonnel(personnelDTO))
            .with(d -> d.setVision("vision"))
            .with(d -> d.setMission("mission"))
            .with(d -> d.setProblemStatement("problem]"))
            .get();

    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Portfolio.class, fields::add);

        assertThat(fields.size()).isEqualTo(PortfolioDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_be_equal() {
        Portfolio portfolio2 = new Portfolio();
        BeanUtils.copyProperties(portfolio, portfolio2);

        assertEquals(portfolio, portfolio);
        assertNotEquals(portfolio, null);
        assertNotEquals(portfolio, new User());
        assertNotEquals(portfolio, new Portfolio());
        assertEquals(portfolio, portfolio2);
    }

    @Test
    void should_get_properties() {
        assertThat(portfolio.getId()).isEqualTo(1L);
        assertThat(portfolio.getName()).isEqualTo("portfolio name");
        assertThat(portfolio.getDescription()).isEqualTo("description");
        assertFalse(portfolio.getIsArchived());
    }

    @Test
    void can_return_dto() {
        assertThat(portfolio.toDto()).isEqualTo(portfolioDTO);
    }
}
