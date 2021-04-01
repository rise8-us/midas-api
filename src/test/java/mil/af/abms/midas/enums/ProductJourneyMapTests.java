package mil.af.abms.midas.enums;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

public class ProductJourneyMapTests {

    @Test
    public void canStreamEnum() {
        assertThat(ProductJourneyMap.stream().collect(Collectors.toList()).size()).isEqualTo(4);
    }

    @Test
    public void can_get_journey_map_by_long() {
        Map<ProductJourneyMap, Boolean> journeyMap = new HashMap<ProductJourneyMap, Boolean>();
        journeyMap.put(ProductJourneyMap.COT, true);
        journeyMap.put(ProductJourneyMap.GIT_PIPELINE, true);
        journeyMap.put(ProductJourneyMap.CTF, false);
        journeyMap.put(ProductJourneyMap.PRODUCTION, false);

        assertThat(ProductJourneyMap.getJourneyMap(3L)).isEqualTo(journeyMap);
    }

    @Test
    public void can_update_journey_map() {
        Map<ProductJourneyMap, Boolean> journeyMap = new HashMap<ProductJourneyMap, Boolean>();
        journeyMap.put(ProductJourneyMap.PRODUCTION, true);

        assertThat(ProductJourneyMap.setJourneyMap(0L, journeyMap)).isEqualTo(8);
    }

    @Test
    public void should_return_expected_enum_journey_map() {
        assertThat(ProductJourneyMap.COT.getOffset()).isEqualTo(0);
        assertThat(ProductJourneyMap.COT.getName()).isEqualTo("COT");
        assertThat(ProductJourneyMap.COT.getDescription()).isEqualTo("has their COT");
        assertThat(ProductJourneyMap.COT.getBitValue()).isEqualTo(1);
    }

    @Test
    public void should_return_expected_enum_add() {
        assertThat(ProductJourneyMap.GIT_PIPELINE.getOffset()).isEqualTo(1);
        assertThat(ProductJourneyMap.GIT_PIPELINE.getName()).isEqualTo("GIT_PIPELINE");
        assertThat(ProductJourneyMap.GIT_PIPELINE.getDescription()).isEqualTo("has pipelines in gitlab");
        assertThat(ProductJourneyMap.GIT_PIPELINE.getBitValue()).isEqualTo(2);
    }

}
