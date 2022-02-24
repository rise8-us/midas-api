package mil.af.abms.midas.api.measure;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.completion.Completion;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.measure.dto.MeasureDTO;
import mil.af.abms.midas.enums.CompletionType;
import mil.af.abms.midas.enums.ProgressionStatus;

public class MeasureTests {

    private final LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2021-07-09");
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(3L))
            .get();
    private final Completion completion = Builder.build(Completion .class)
            .with(c -> c.setValue(0F))
            .with(c -> c.setTarget(1F))
            .with(c -> c.setCompletionType(CompletionType.BINARY))
            .with(c -> c.setDueDate(null))
            .with(c -> c.setStartDate(null))
            .get();
    private final Measure measure = Builder.build(Measure.class)
            .with(m -> m.setId(1L))
            .with(m -> m.setText("First"))
            .with(m -> m.setAssertion(assertion))
            .with(m -> m.setComments(Set.of()))
            .with(m -> m.setStatus(ProgressionStatus.ON_TRACK))
            .with(m -> m.setCompletion(completion))
            .get();
    private final MeasureDTO measureDTO = Builder.build(MeasureDTO.class)
            .with(m -> m.setId(measure.getId()))
            .with(m -> m.setCreationDate(measure.getCreationDate()))
            .with(m -> m.setText(measure.getText()))
            .with(m -> m.setAssertionId(assertion.getId()))
            .with(m -> m.setCommentIds(Set.of()))
            .with(m -> m.setStatus(ProgressionStatus.ON_TRACK))
            .with(m -> m.setCompletion(completion.toDto()))
            .get();

    @Test
    void should_have_all_measureDTO_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Measure.class, fields::add);

        assertThat(fields.size()).isEqualTo(MeasureDTO.class.getDeclaredFields().length);
    }

    @Test
    void should_set_and_get_properties() {
        assertThat(measure.getId()).isEqualTo(1L);
        assertThat(measure.getCompletion().getCompletionType()).isEqualTo(CompletionType.BINARY);
        assertThat(measure.getText()).isEqualTo("First");
    }

    @Test
    void should_return_dto() {
        assertThat(measure.toDto()).isEqualTo(measureDTO);
    }

}
