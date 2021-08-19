package mil.af.abms.midas.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import java.util.Map;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.search.SearchOperation;

@Slf4j
public class LocalDateTimeParsingStrategy implements ParsingStrategy {

    @Override
    public Predicate makePredicate(
            SearchOperation operation,
            Path<?> nestedRoot,
            String criteriaKey,
            String value,
            CriteriaBuilder builder) {


        Map<SearchOperation, Supplier<Predicate>> predicates = Map.ofEntries(
                Map.entry(SearchOperation.NULL, () -> builder.isNull(nestedRoot.get(criteriaKey))),
                Map.entry(SearchOperation.NOT_NULL, () -> builder.isNotNull(nestedRoot.get(criteriaKey))),
                Map.entry(SearchOperation.EQUALS, () -> builder.equal(nestedRoot.get(criteriaKey), TimeConversion.getTime(value))),
                Map.entry(SearchOperation.NOT_EQUALS, () -> builder.notEqual(nestedRoot.get(criteriaKey), TimeConversion.getTime(value))),
                Map.entry(SearchOperation.GREATER_THAN, () -> builder.greaterThan(nestedRoot.get(criteriaKey), TimeConversion.getTime(value))),
                Map.entry(SearchOperation.GREATER_THAN_OR_EQUAL, () -> builder.greaterThanOrEqualTo(nestedRoot.get(criteriaKey), TimeConversion.getTime(value))),
                Map.entry(SearchOperation.LESS_THAN, () -> builder.lessThan(nestedRoot.get(criteriaKey), TimeConversion.getTime(value))),
                Map.entry(SearchOperation.LESS_THAN_OR_EQUAL, () -> builder.lessThanOrEqualTo(nestedRoot.get(criteriaKey), TimeConversion.getTime(value)))
        );
        return predicates.getOrDefault(operation, () -> null).get();



    }

}
