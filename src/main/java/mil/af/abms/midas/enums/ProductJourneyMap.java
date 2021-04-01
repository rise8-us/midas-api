package mil.af.abms.midas.enums;

import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Stream;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProductJourneyMap {
    COT(0, "COT", "has their COT"),
    GIT_PIPELINE(1, "GIT_PIPELINE", "has pipelines in gitlab"),
    CTF(2, "CTF", "has a CTF"),
    PRODUCTION(3, "PRODUCTION", "can deploy to production");

    private final Integer offset;
    private final String name;
    private final String description;

    public static Stream<ProductJourneyMap> stream() {
        return Stream.of(ProductJourneyMap.values());
    }

    public static Map<ProductJourneyMap, Boolean> getJourneyMap(Long journeyLong) {
        Map<ProductJourneyMap, Boolean> journeyMap = new EnumMap<>(ProductJourneyMap.class);
        ProductJourneyMap.stream().forEach(p -> journeyMap.put(p, (journeyLong & p.getBitValue()) > 0));
        return journeyMap;
    }

    public static Long setJourneyMap(Long currentLong, Map<ProductJourneyMap, Boolean> updatedProductJourneyMap) {
        Map<ProductJourneyMap, Boolean> currentProductJourneyMap = getJourneyMap(currentLong);
        updatedProductJourneyMap.forEach(currentProductJourneyMap::replace);
        return ProductJourneyMap.stream().filter(currentProductJourneyMap::get).mapToLong(ProductJourneyMap::getBitValue).sum();
    }

    public Long getBitValue() {
        return Math.round(Math.pow(2, offset));
    }

}
