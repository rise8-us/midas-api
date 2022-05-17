package mil.af.abms.midas.api.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.gantt.target.Target;
import mil.af.abms.midas.api.gantt.target.TargetService;
import mil.af.abms.midas.api.gantt.target.dto.UpdateTargetDTO;
import mil.af.abms.midas.api.helper.Builder;

@ExtendWith(SpringExtension.class)
@Import({IsValidGanttDueDateValidator.class})
public class IsValidGanttDueDateValidatorTests {

    @Autowired
    IsValidGanttDueDateValidator validator;
    @MockBean
    private TargetService targetService;
    @MockBean
    private UpdateTargetDTO dto;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    private final LocalDate today = LocalDate.now();
    private final Target target = Builder.build(Target.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setTitle("title"))
            .with(t -> t.setDescription("description"))
            .with(t -> t.setStartDate(today))
            .with(t -> t.setDueDate(null))
            .get();
    UpdateTargetDTO targetDTOWithValidDueDate = new UpdateTargetDTO(today, today.plusDays(1L), "updated title", "updated description", Set.of(), Set.of(), false);
    UpdateTargetDTO targetDTOWithInValidDueDate = new UpdateTargetDTO(today, today.minusDays(1L), "updated title", "updated description", Set.of(), Set.of(), false);

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @Test
    public void should_validate_due_date_true() {
        Target updatedTarget = new Target();
        BeanUtils.copyProperties(target, updatedTarget);
        when(targetService.updateById(1L, targetDTOWithValidDueDate)).thenReturn(updatedTarget);

        assertTrue(validator.isValid(targetDTOWithValidDueDate, context));
    }

    @Test
    public void should_validate_false_when_due_date_is_more_recent_than_start_date() {
        Target updatedTarget = new Target();
        BeanUtils.copyProperties(target, updatedTarget);
        when(targetService.updateById(1L, targetDTOWithInValidDueDate)).thenReturn(updatedTarget);

        assertFalse(validator.isValid(targetDTOWithInValidDueDate, context));
    }

}
