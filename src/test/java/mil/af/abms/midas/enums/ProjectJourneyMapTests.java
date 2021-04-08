package mil.af.abms.midas.enums;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class ProjectJourneyMapTests {

    @Test
    public void canStreamEnum() {
        assertThat(ProjectJourneyMap.stream().collect(Collectors.toList()).size()).isEqualTo(4);
    }

    @Test
    public void can_get_journey_map_by_long() {
        Map<ProjectJourneyMap, Boolean> journeyMap = new HashMap<ProjectJourneyMap, Boolean>();
        journeyMap.put(ProjectJourneyMap.COT, true);
        journeyMap.put(ProjectJourneyMap.GIT_PIPELINE, true);
        journeyMap.put(ProjectJourneyMap.CTF, false);
        journeyMap.put(ProjectJourneyMap.PRODUCTION, false);

        assertThat(ProjectJourneyMap.getJourneyMap(3L)).isEqualTo(journeyMap);
    }

    @Test
    public void can_update_journey_map() {
        Map<ProjectJourneyMap, Boolean> journeyMap = new HashMap<ProjectJourneyMap, Boolean>();
        journeyMap.put(ProjectJourneyMap.PRODUCTION, true);

        assertThat(ProjectJourneyMap.setJourneyMap(0L, journeyMap)).isEqualTo(8);
    }

    @Test
    public void should_return_expected_enum_journey_map() {
        assertThat(ProjectJourneyMap.COT.getOffset()).isEqualTo(0);
        assertThat(ProjectJourneyMap.COT.getName()).isEqualTo("COT");
        assertThat(ProjectJourneyMap.COT.getDescription()).isEqualTo("has their COT");
        assertThat(ProjectJourneyMap.COT.getBitValue()).isEqualTo(1);
    }

    @Test
    public void should_return_expected_enum_add() {
        assertThat(ProjectJourneyMap.GIT_PIPELINE.getOffset()).isEqualTo(1);
        assertThat(ProjectJourneyMap.GIT_PIPELINE.getName()).isEqualTo("GIT_PIPELINE");
        assertThat(ProjectJourneyMap.GIT_PIPELINE.getDescription()).isEqualTo("has pipelines in gitlab");
        assertThat(ProjectJourneyMap.GIT_PIPELINE.getBitValue()).isEqualTo(2);
    }

}
