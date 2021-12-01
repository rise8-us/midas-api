package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CompletionTypeTests {

    @Test
    void should_have_5_values() {
        assertThat(CompletionType.values().length).isEqualTo(5);
    }

    @Test
    void should_get_fields() {
        assertThat(CompletionType.STRING.getName()).isEqualTo("STRING");
        assertThat(CompletionType.STRING.getDisplayName()).isEqualTo("String");
        assertThat(CompletionType.STRING.getDescription()).isEqualTo("manual text entry");
        assertThat(CompletionType.BINARY.getName()).isEqualTo("BINARY");
        assertThat(CompletionType.BINARY.getDisplayName()).isEqualTo("Binary");
        assertThat(CompletionType.BINARY.getDescription()).isEqualTo("Complete? true or false");
        assertThat(CompletionType.PERCENTAGE.getName()).isEqualTo("PERCENTAGE");
        assertThat(CompletionType.PERCENTAGE.getDisplayName()).isEqualTo("Percentage");
        assertThat(CompletionType.PERCENTAGE.getDescription()).isEqualTo("Percentage of completeness");
        assertThat(CompletionType.NUMBER.getName()).isEqualTo("NUMBER");
        assertThat(CompletionType.NUMBER.getDisplayName()).isEqualTo("Number");
        assertThat(CompletionType.NUMBER.getDescription()).isEqualTo("Numerical representation of completeness");
        assertThat(CompletionType.MONEY.getName()).isEqualTo("MONEY");
        assertThat(CompletionType.MONEY.getDisplayName()).isEqualTo("Money");
        assertThat(CompletionType.MONEY.getDescription()).isEqualTo("Monetary representation of completeness");
    }
}
