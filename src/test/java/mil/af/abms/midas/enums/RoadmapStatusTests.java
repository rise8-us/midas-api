package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RoadmapStatusTests {

    @Test
    void should_have_5_values() {
        assertThat(RoadmapStatus.values().length).isEqualTo(3);
    }

    @Test
    void should_get_fields() {
        assertThat(RoadmapStatus.NOT_STARTED.getName()).isEqualTo("NOT_STARTED");
        assertThat(RoadmapStatus.NOT_STARTED.getLabel()).isEqualTo("Not Started");
        assertThat(RoadmapStatus.NOT_STARTED.getColor()).isEqualTo("#969696");
    }
}
