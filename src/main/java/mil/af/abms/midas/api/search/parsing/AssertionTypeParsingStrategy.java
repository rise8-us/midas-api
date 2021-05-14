package mil.af.abms.midas.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import mil.af.abms.midas.api.search.SearchOperation;
import mil.af.abms.midas.enums.AssertionType;

public class AssertionTypeParsingStrategy implements ParsingStrategy {

    @Override
    public Predicate makePredicate(
            SearchOperation operation,
            Path<?> nestedRoot,
            String criteriaKey,
            String value,
            CriteriaBuilder builder) {

        switch (operation) {
            case EQUALS:
                return builder.equal(nestedRoot.get(criteriaKey), AssertionType.valueOf(value));
            case NOT_EQUALS:
                return builder.notEqual(nestedRoot.get(criteriaKey), AssertionType.valueOf(value));
            default:
                return null;
        }
    }

}
