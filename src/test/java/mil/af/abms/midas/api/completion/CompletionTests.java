package mil.af.abms.midas.api.completion;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ReflectionUtils;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.completion.dto.CompletionDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.CompletionType;

class CompletionTests {

    private static final int ENTITY_DTO_FIELD_OFFSET = 3;

    private final Completion completion = Builder.build(Completion.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setCreationDate(LocalDateTime.now()))
            .with(c -> c.setStartDate(LocalDate.now()))
            .with(c -> c.setDueDate(LocalDate.now().plusDays(1)))
            .with(c -> c.setCompletedAt(null))
            .with(c -> c.setCompletionType(CompletionType.BINARY))
            .with(c -> c.setValue(0F))
            .with(c -> c.setTarget(1F))
            .get();
    private final CompletionDTO completionDTO = Builder.build(CompletionDTO.class)
            .with(c -> c.setId(1L))
            .with(c -> c.setStartDate(completion.getStartDate()))
            .with(c -> c.setDueDate(completion.getDueDate()))
            .with(c -> c.setCompletedAt(completion.getCompletedAt()))
            .with(c -> c.setCompletionType(completion.getCompletionType()))
            .with(c -> c.setValue(completion.getValue()))
            .with(c -> c.setTarget(completion.getTarget()))
            .get();

    @Test
    void should_have_all_dto_fields() {
        List<Field> fields = new LinkedList<>();
        ReflectionUtils.doWithFields(Completion.class, fields::add);

        assertThat(fields.size()).isEqualTo(CompletionDTO.class.getDeclaredFields().length + ENTITY_DTO_FIELD_OFFSET);
    }

    @Test
    void should_be_equal() {
        Completion completion2 = new Completion();
        BeanUtils.copyProperties(completion, completion2);

        Assertions.assertThat(completion).isEqualTo(completion);
        Assertions.assertThat(completion).isNotNull();
        Assertions.assertThat(completion).isNotEqualTo(new User());
        Assertions.assertThat(completion).isNotSameAs(new Completion());
        Assertions.assertThat(completion).isEqualTo(completion2);
    }

    @Test
    void should_get_properties() {
        assertThat(completion.getId()).isEqualTo(1L);
    }

    @Test
    void can_return_dto() { assertThat(completion.toDto()).isEqualTo(completionDTO); }

}
