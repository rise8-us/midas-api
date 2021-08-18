package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CompletionTypeTests {

    @Test
    void should_have_2_values() {
        assertThat(CompletionType.values().length).isEqualTo(3);
    }

    @Test
    void should_get_fields() {
        assertThat(CompletionType.STRING.getDisplayName()).isEqualTo("String");
        assertThat(CompletionType.STRING.getDescription()).isEqualTo("manual text entry");
        assertThat(CompletionType.BINARY.getDisplayName()).isEqualTo("Binary");
        assertThat(CompletionType.BINARY.getDescription()).isEqualTo("Complete? true or false");
        assertThat(CompletionType.PERCENTAGE.getDisplayName()).isEqualTo("Percentage");
        assertThat(CompletionType.PERCENTAGE.getDescription()).isEqualTo("Percentage of completeness");
    }
}
