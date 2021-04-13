package mil.af.abms.midas.api.portfolio;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.application.Application;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.user.User;

public class PortfolioTests {

    private static final LocalDateTime TEST_TIME = LocalDateTime.now();

    private User portfolioManager = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private Set<Application> applications = Set.of(Builder.build(Application.class).with(u -> u.setId(3L)).get());
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("test portfolio"))
            .with(p -> p.setCreationDate(TEST_TIME))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setPortfolioManager(portfolioManager))
            .with(p -> p.setApplications(applications)).get();
    private final PortfolioDTO portfolioDTO = Builder.build(PortfolioDTO.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("Midas"))
            .with(p -> p.setDescription("test portfolio"))
            .with(p -> p.setCreationDate(TEST_TIME))
            .with(p -> p.setIsArchived(false))
            .with(p -> p.setPortfolioManagerId(portfolioManager.getId()))
            .with(p -> p.setApplicationIds(Set.of(3L))).get();

    @Test
    public void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Portfolio.class, fields::add);

        assertThat(fields.size()).isEqualTo(PortfolioDTO.class.getDeclaredFields().length);
    }

    @Test
    public void should_be_equal() {
        Portfolio portfolio2 = new Portfolio();
        BeanUtils.copyProperties(portfolio, portfolio2);

        assertEquals(portfolio, portfolio);
        assertNotEquals(null, portfolio);
        assertNotEquals(portfolio, new User());
        assertNotEquals(portfolio, new Portfolio());
        assertEquals(portfolio, portfolio2);
    }

    @Test
    public void should_get_properties() {
        assertThat(portfolio.getId()).isEqualTo(1L);
        assertThat(portfolio.getName()).isEqualTo("Midas");
        assertThat(portfolio.getDescription()).isEqualTo("test portfolio");
        assertThat(portfolio.getCreationDate()).isEqualTo(TEST_TIME);
        assertFalse(portfolio.getIsArchived());
        assertThat(portfolio.getPortfolioManager()).isEqualTo(portfolioManager);
        assertThat(portfolio.getApplications()).isEqualTo(applications);
    }

    @Test
    public void can_return_dto() {
        assertThat(portfolio.toDto()).isEqualTo(portfolioDTO);
    }

    @Test
    public void should_return_null_lead() {
        Portfolio nullLead = new Portfolio();
        BeanUtils.copyProperties(portfolio, nullLead);
        nullLead.setPortfolioManager(null);

        assertThat(nullLead.toDto().getPortfolioManagerId()).isEqualTo(null);
    }
}
