package mil.af.abms.midas.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import java.util.Map;
import java.util.function.Supplier;

import mil.af.abms.midas.api.search.SearchOperation;

public class LongParsingStrategy implements ParsingStrategy {

    @Override
    public Predicate makePredicate(SearchOperation operation, Path<?> nestedRoot, String criteriaKey, String valueStr, CriteriaBuilder builder) {

        Map<SearchOperation, Supplier<Predicate>> predicates = Map.ofEntries(
                Map.entry(SearchOperation.EQUALS, () -> builder.equal(nestedRoot.get(criteriaKey), Long.valueOf(valueStr))),
                Map.entry(SearchOperation.NOT_EQUALS, () -> builder.notEqual(nestedRoot.get(criteriaKey), Long.valueOf(valueStr))),
                Map.entry(SearchOperation.GREATER_THAN, () -> builder.greaterThan(nestedRoot.get(criteriaKey), Long.valueOf(valueStr))),
                Map.entry(SearchOperation.GREATER_THAN_OR_EQUAL, () -> builder.greaterThanOrEqualTo(nestedRoot.get(criteriaKey), Long.valueOf(valueStr))),
                Map.entry(SearchOperation.LESS_THAN, () -> builder.lessThan(nestedRoot.get(criteriaKey), Long.valueOf(valueStr))),
                Map.entry(SearchOperation.LESS_THAN_OR_EQUAL, () -> builder.lessThanOrEqualTo(nestedRoot.get(criteriaKey), Long.valueOf(valueStr)))
        );

        return predicates.getOrDefault(operation, () -> null).get();
    }

}
