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
        assertThat(RoadmapStatus.FUTURE.getName()).isEqualTo("FUTURE");
        assertThat(RoadmapStatus.FUTURE.getLabel()).isEqualTo("Future");
        assertThat(RoadmapStatus.FUTURE.getColor()).isEqualTo("#797979");
    }
}
