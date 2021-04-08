package mil.af.abms.midas.enums;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProjectJourneyMap {
    COT(0, "COT", "has their COT"),
    GIT_PIPELINE(1, "GIT_PIPELINE", "has pipelines in gitlab"),
    CTF(2, "CTF", "has a CTF"),
    PRODUCTION(3, "PRODUCTION", "can deploy to production");

    private final Integer offset;
    private final String name;
    private final String description;

    public static Stream<ProjectJourneyMap> stream() {
        return Stream.of(ProjectJourneyMap.values());
    }

    public static Map<ProjectJourneyMap, Boolean> getJourneyMap(Long journeyLong) {
        Map<ProjectJourneyMap, Boolean> journeyMap = new EnumMap<>(ProjectJourneyMap.class);
        ProjectJourneyMap.stream().forEach(p -> journeyMap.put(p, (journeyLong & p.getBitValue()) > 0));
        return journeyMap;
    }

    public static Long setJourneyMap(Long currentLong, Map<ProjectJourneyMap, Boolean> updatedProjectJourneyMap) {
        Map<ProjectJourneyMap, Boolean> currentProjectJourneyMap = getJourneyMap(currentLong);
        updatedProjectJourneyMap.forEach(currentProjectJourneyMap::replace);
        return ProjectJourneyMap.stream().filter(currentProjectJourneyMap::get).mapToLong(ProjectJourneyMap::getBitValue).sum();
    }

    public Long getBitValue() {
        return Math.round(Math.pow(2, offset));
    }

}
