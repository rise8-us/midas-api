package us.rise8.mixer.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import us.rise8.mixer.api.search.SearchOperation;

public class StringParsingStrategy implements ParsingStrategy {

    @Override
    public Predicate makePredicate(
            SearchOperation operation,
            Path<?> nestedRoot,
            String criteriaKey,
            String value,
            CriteriaBuilder builder) {

        switch (operation) {
            case EQUALS:
                return builder.equal(nestedRoot.get(criteriaKey), value);
            case NOT_EQUALS:
                return builder.notEqual(nestedRoot.get(criteriaKey), value);
            case GREATER_THAN:
                return builder.greaterThan(nestedRoot.get(criteriaKey), value);
            case LESS_THAN:
                return builder.lessThan(nestedRoot.get(criteriaKey), value);
            case STARTS_WITH:
                return builder.like(nestedRoot.get(criteriaKey), value + "%");
            case ENDS_WITH:
                return builder.like(nestedRoot.get(criteriaKey), "%" + value);
            case CONTAINS:
                return builder.like(nestedRoot.get(criteriaKey), "%" + value + "%");
            case DOESNT_START_WITH:
                return builder.notLike(nestedRoot.get(criteriaKey), value + "%");
            case DOESNT_END_WITH:
                return builder.notLike(nestedRoot.get(criteriaKey), "%" + value);
            case DOESNT_CONTAIN:
                return builder.notLike(nestedRoot.get(criteriaKey), "%" + value + "%");
            default:
                return null;
        }
    }

}
