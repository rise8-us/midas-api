package mil.af.abms.midas.enums;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RoadmapTypeTests {

    @Test
    void should_have_5_values() {
        assertThat(RoadmapType.values().length).isEqualTo(2);
    }

    @Test
    void should_get_fields() {
        assertThat(RoadmapType.GITLAB.getName()).isEqualTo("GITLAB");
        assertThat(RoadmapType.GITLAB.getDisplayName()).isEqualTo("GitLab Epics");
        assertThat(RoadmapType.GITLAB.getDescription()).isEqualTo("All GitLab epics assigned to the group");
        assertThat(RoadmapType.MANUAL.getName()).isEqualTo("MANUAL");
        assertThat(RoadmapType.MANUAL.getDisplayName()).isEqualTo("Manual");
        assertThat(RoadmapType.MANUAL.getDescription()).isEqualTo("Manual entry of roadmap not tied to any source control");
    }
}
