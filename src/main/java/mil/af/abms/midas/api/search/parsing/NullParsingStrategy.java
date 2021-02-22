package mil.af.abms.midas.api.search.parsing;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.search.SearchOperation;


@Slf4j
public class NullParsingStrategy implements ParsingStrategy {

    @Override
    public Predicate makePredicate(SearchOperation operation, Path<?> nestedRoot, String criteriaKey, String value, CriteriaBuilder builder) {
        log.warn("No parsing strategy found for type " + nestedRoot.get(criteriaKey).getJavaType().getSimpleName());
        return null;
    }

}
