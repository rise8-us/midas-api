package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class AssertionTypeTests {

    @Test
    public void should_have_4_values() {
        assertThat(AssertionType.values().length).isEqualTo(4);
    }

    @Test
    public void should_get_fields() {
        assertThat(AssertionType.OBJECTIVE.getDisplayName()).isEqualTo("Objective");
        assertThat(AssertionType.OBJECTIVE.getDescription()).isEqualTo("Defining an over-arching breakthrough vision");
        assertThat(AssertionType.OBJECTIVE.getDetail()).isEqualTo("Stable, concise and linked to company mission");
    }
}
