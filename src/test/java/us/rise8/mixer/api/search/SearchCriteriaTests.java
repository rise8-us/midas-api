package us.rise8.mixer.api.search;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

public class SearchCriteriaTests {

    @Test
    public void shouldGetFields() {
        SearchCriteria criteria = new SearchCriteria("user.username", ":", null, "yoda", null);

        assertThat(criteria.getKey()).isEqualTo("user.username");
        assertThat(criteria.getOperation()).isEqualTo(SearchOperation.EQUALS);
        assertThat(criteria.getValue()).isEqualTo("yoda");
    }

    @Test
    public void shouldOperationToEnum() {
        for (String op : SearchOperation.getSIMPLE_OPERATION_SET()) {
            SearchCriteria criteria = new SearchCriteria("user", op, null, "yoda", null);

            assertThat(criteria.getOperation()).isEqualTo(SearchOperation.getSimpleOperation(op));
        }

    }

    @Test
    public void shouldHandleOperationEqualsPrefixSuffix() {
        SearchCriteria criteriaStartWith = new SearchCriteria("user", ":", "*", "yoda", null);
        SearchCriteria criteriaContains = new SearchCriteria("user", ":", "*", "yoda", "*");
        SearchCriteria criteriaEndsWith = new SearchCriteria("user", ":", null, "yoda", "*");

        assertThat(criteriaStartWith.getOperation()).isEqualTo(SearchOperation.ENDS_WITH);
        assertThat(criteriaContains.getOperation()).isEqualTo(SearchOperation.CONTAINS);
        assertThat(criteriaEndsWith.getOperation()).isEqualTo(SearchOperation.STARTS_WITH);
    }

    @Test
    public void shouldHandleOperationNotEqualsPrefixSuffix() {
        SearchCriteria criteriaStartWith = new SearchCriteria("user", "!", "*", "yoda", null);
        SearchCriteria criteriaContains = new SearchCriteria("user", "!", "*", "yoda", "*");
        SearchCriteria criteriaEndsWith = new SearchCriteria("user", "!", null, "yoda", "*");

        assertThat(criteriaStartWith.getOperation()).isEqualTo(SearchOperation.DOESNT_END_WITH);
        assertThat(criteriaContains.getOperation()).isEqualTo(SearchOperation.DOESNT_CONTAIN);
        assertThat(criteriaEndsWith.getOperation()).isEqualTo(SearchOperation.DOESNT_START_WITH);
    }

}
