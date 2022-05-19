package mil.af.abms.midas.api.gantt.win;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.gantt.win.dto.WinDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.user.User;

public class WinTests {

    private final LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2021-07-09");
    private final Portfolio portfolio = Builder.build(Portfolio.class)
            .with(p -> p.setId(1L))
            .with(p -> p.setName("portfolio name"))
            .with(p -> p.setDescription("description"))
            .get();
    private final Win win = Builder.build(Win.class)
            .with(m -> m.setId(2L))
            .with(m -> m.setDueDate(DUE_DATE))
            .with(m -> m.setTitle("winTitle"))
            .with(m -> m.setDescription("winDescription"))
            .with(m -> m.setPortfolio(portfolio))
            .get();

    private final WinDTO winDTO = Builder.build(WinDTO.class)
            .with(m -> m.setId(2L))
            .with(m -> m.setDueDate(win.getDueDate()))
            .with(m -> m.setTitle(win.getTitle()))
            .with(m -> m.setDescription(win.getDescription()))
            .with(m -> m.setPortfolioId(1L))
            .get();

    @Test
    void should_have_all_win_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Win.class, fields::add);

        assertThat(fields.size()).isEqualTo(6);
    }

    @Test
    void should_get_properties() {
        assertThat(win.getId()).isEqualTo(2L);
        assertThat(win.getDueDate()).isEqualTo(DUE_DATE);
        assertThat(win.getTitle()).isEqualTo("winTitle");
        assertThat(win.getDescription()).isEqualTo("winDescription");
        assertThat(win.getPortfolio()).isEqualTo(portfolio);
    }

    @Test
    void should_be_equal() {
        Win win2 = new Win();
        BeanUtils.copyProperties(win, win2);

        assertEquals(win, win);
        assertNotEquals(null, win);
        assertNotEquals(win, new User());
        assertNotEquals(win, new Win());
        assertEquals(win, win2);
        assertFalse(win.equals(null));
    }

    @Test
    void should_return_dto() {
        assertThat(win.toDto()).isEqualTo(winDTO);
    }

}
