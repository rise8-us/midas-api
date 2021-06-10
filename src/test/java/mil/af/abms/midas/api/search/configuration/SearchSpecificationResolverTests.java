package mil.af.abms.midas.api.search.configuration;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Method;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;

import org.junit.jupiter.api.Test;

public class SearchSpecificationResolverTests {

    SearchSpecificationResolver resolver = new SearchSpecificationResolver();

    @Test
    public void should_return_false_on_buildSpecification() {
        assertThat(resolver.buildSpecification("")).isNull();
        assertThat(resolver.buildSpecification(null)).isNull();
    }

    @Test
    void should_return_false_on_supportsParameter_when_bad_param() throws Exception{
        Class<String> clazz = String.class;
        Method equals = clazz.getMethod("equals", Object.class);
        assertFalse(resolver.supportsParameter(new MethodParameter(equals, 0)));
    }

    @Test
    void should_return_false_on_supportsParameter_when_bad_annotation() throws Exception{
        Class<Specification> clazz = Specification.class;
        Method not = clazz.getMethod("not", Specification.class);
        assertFalse(resolver.supportsParameter(new MethodParameter(not, 0)));
    }

}
