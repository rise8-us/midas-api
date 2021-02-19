package us.rise8.mixer.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import us.rise8.mixer.api.search.SearchOperation;

public class LongParsingStrategy implements ParsingStrategy {

    @Override
    public Predicate makePredicate(SearchOperation operation, Path<?> nestedRoot, String criteriaKey, String value, CriteriaBuilder builder) {
        switch (operation) {
            case EQUALS:
                return builder.equal(nestedRoot.get(criteriaKey), Long.valueOf(value));
            case NOT_EQUALS:
                return builder.notEqual(nestedRoot.get(criteriaKey), Long.valueOf(value));
            case GREATER_THAN:
                return builder.greaterThan(nestedRoot.get(criteriaKey), Long.valueOf(value));
            case GREATER_THAN_OR_EQUAL:
                return builder.greaterThanOrEqualTo(nestedRoot.get(criteriaKey), Long.valueOf(value));
            case LESS_THAN:
                return builder.lessThan(nestedRoot.get(criteriaKey), Long.valueOf(value));
            case LESS_THAN_OR_EQUAL:
                return builder.lessThanOrEqualTo(nestedRoot.get(criteriaKey), Long.valueOf(value));
            default:
                return null;
        }
    }

}
