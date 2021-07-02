package mil.af.abms.midas.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import java.util.Map;

import mil.af.abms.midas.api.search.SearchOperation;

public interface ParsingStrategy {

    static final  Map<String, ParsingStrategy> parsingStrategies = Map.ofEntries(
            Map.entry("String", new StringParsingStrategy()),
            Map.entry("Long", new LongParsingStrategy()),
            Map.entry("AssertionStatus", new AssertionStatusParsingStrategy()),
            Map.entry("AssertionType", new AssertionTypeParsingStrategy()),
            Map.entry("ProductType", new ProductTypeParsingStrategy())
    );

    static ParsingStrategy getStrategy(Path<?> node) {
        String javaType = node.getJavaType().getSimpleName();
        return parsingStrategies.getOrDefault(javaType, new NullParsingStrategy());
    }

    Predicate makePredicate(
            SearchOperation operation,
            Path<?> nestedRoot,
            String criteriaKey,
            String value,
            CriteriaBuilder builder
    );
}
