package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class AssertionStatusTests {

    @Test
    public void should_have_6_values() {
        assertThat(AssertionStatus.values().length).isEqualTo(6);
    }

    @Test
    public void should_get_fields() {
        assertThat(AssertionStatus.NOT_STARTED.getName()).isEqualTo("NOT_STARTED");
        assertThat(AssertionStatus.NOT_STARTED.getLabel()).isEqualTo("Not Started");
        assertThat(AssertionStatus.NOT_STARTED.getColor()).isEqualTo("#969696");
    }
}
