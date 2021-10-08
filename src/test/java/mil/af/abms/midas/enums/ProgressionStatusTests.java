package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class ProgressionStatusTests {

    @Test
    public void should_have_5_values() {
        assertThat(ProgressionStatus.values().length).isEqualTo(5);
    }

    @Test
    public void should_get_fields() {
        assertThat(ProgressionStatus.NOT_STARTED.getName()).isEqualTo("NOT_STARTED");
        assertThat(ProgressionStatus.NOT_STARTED.getLabel()).isEqualTo("Not Started");
        assertThat(ProgressionStatus.NOT_STARTED.getColor()).isEqualTo("#969696");
    }
}
