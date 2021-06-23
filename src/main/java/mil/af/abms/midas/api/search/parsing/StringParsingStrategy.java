package mil.af.abms.midas.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import java.util.Map;
import java.util.function.Supplier;

import mil.af.abms.midas.api.search.SearchOperation;

public class StringParsingStrategy implements ParsingStrategy {
    
    @Override
    public Predicate makePredicate(SearchOperation operation, Path<?> nestedRoot, String criteriaKey, String value, CriteriaBuilder builder) {

        Map<SearchOperation, Supplier<Predicate>> predicates = Map.ofEntries(
                Map.entry(SearchOperation.EQUALS, () -> builder.equal(nestedRoot.get(criteriaKey), value)),
                Map.entry(SearchOperation.NOT_EQUALS, () -> builder.notEqual(nestedRoot.get(criteriaKey), value)),
                Map.entry(SearchOperation.GREATER_THAN, () -> builder.greaterThan(nestedRoot.get(criteriaKey), value)),
                Map.entry(SearchOperation.LESS_THAN, () -> builder.lessThan(nestedRoot.get(criteriaKey), value)),
                Map.entry(SearchOperation.STARTS_WITH, () -> builder.like(nestedRoot.get(criteriaKey), value + "%")),
                Map.entry(SearchOperation.ENDS_WITH, () -> builder.like(nestedRoot.get(criteriaKey), "%" + value)),
                Map.entry(SearchOperation.CONTAINS, () -> builder.like(nestedRoot.get(criteriaKey), "%" + value + "%")),
                Map.entry(SearchOperation.DOESNT_START_WITH, () -> builder.notLike(nestedRoot.get(criteriaKey), value + "%")),
                Map.entry(SearchOperation.DOESNT_END_WITH, () -> builder.notLike(nestedRoot.get(criteriaKey), "%" + value)),
                Map.entry(SearchOperation.DOESNT_CONTAIN, () -> builder.notLike(nestedRoot.get(criteriaKey), "%" + value + "%"))
        );

        return predicates.getOrDefault(operation, () -> null).get();

    }

}
