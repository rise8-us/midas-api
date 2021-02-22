package mil.af.abms.midas.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import mil.af.abms.midas.api.search.SearchOperation;

public interface ParsingStrategy {

    static ParsingStrategy getStrategy(Path<?> node) {
        String javaType = node.getJavaType().getSimpleName();
        switch (javaType) {
            case "String":
                return new StringParsingStrategy();
            case "Long":
                return new LongParsingStrategy();
            default:
                return new NullParsingStrategy();
        }
    }

    Predicate makePredicate(
            SearchOperation operation,
            Path<?> nestedRoot,
            String criteriaKey,
            String value,
            CriteriaBuilder builder
    );
}
