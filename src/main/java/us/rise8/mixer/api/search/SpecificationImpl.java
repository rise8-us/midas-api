package us.rise8.mixer.api.search;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import us.rise8.mixer.api.search.parsing.ParsingStrategy;

public class SpecificationImpl<T> implements Specification<T> {

    private final SearchCriteria criteria;

    public SpecificationImpl(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        String[] nestedKey = criteria.getKey().split("\\.");
        Path<?> nestedRoot = getNestedRoot(root, nestedKey);
        String criteriaKey = nestedKey[nestedKey.length - 1];

        ParsingStrategy strategy = ParsingStrategy.getStrategy(nestedRoot.get(criteriaKey));
        return strategy.makePredicate(criteria.getOperation(), nestedRoot, criteriaKey, criteria.getValue(), builder);

    }

    private Path<?> getNestedRoot(Root<T> root, String[] nestedKey) {
        List<String> prefix = new LinkedList<>(Arrays.asList(nestedKey));
        prefix.remove(nestedKey.length - 1);
        Path<?> temp = root;
        for (String s : prefix) {
            temp = temp.get(s);
        }
        return temp;
    }

}
