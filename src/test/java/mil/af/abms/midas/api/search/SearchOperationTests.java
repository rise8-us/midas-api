package mil.af.abms.midas.api.search;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.junit.jupiter.api.Test;

public class SearchOperationTests {

    @Test
    public void shouldConvertOperationToEnum() {

        assertThat(SearchOperation.getSimpleOperation(":")).isEqualTo(SearchOperation.EQUALS);
        assertThat(SearchOperation.getSimpleOperation("!")).isEqualTo(SearchOperation.NOT_EQUALS);
        assertThat(SearchOperation.getSimpleOperation(">")).isEqualTo(SearchOperation.GREATER_THAN);
        assertThat(SearchOperation.getSimpleOperation(">=")).isEqualTo(SearchOperation.GREATER_THAN_OR_EQUAL);
        assertThat(SearchOperation.getSimpleOperation("<")).isEqualTo(SearchOperation.LESS_THAN);
        assertThat(SearchOperation.getSimpleOperation("<=")).isEqualTo(SearchOperation.LESS_THAN_OR_EQUAL);
        assertThat(SearchOperation.getSimpleOperation("::")).isEqualTo(SearchOperation.INCLUDES);
    }

    @Test
    public void shouldGetSimpleOperators() {
        assertThat(SearchOperation.getSIMPLE_OPERATION_SET().length).isEqualTo(7);
    }
}
