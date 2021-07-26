package mil.af.abms.midas.api.search.configuration;

import java.util.Optional;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import mil.af.abms.midas.api.search.SpecificationsBuilder;
import mil.af.abms.midas.api.search.annotation.SearchSpec;
import mil.af.abms.midas.exception.IllegalRequestHeadersException;

class SearchSpecificationResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        return parameter.getParameterType() == Specification.class && parameter.hasParameterAnnotation(SearchSpec.class);
    }

    @Override
    public Specification<?> resolveArgument(
            @NonNull MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws IllegalRequestHeadersException {
        SearchSpec def = Optional.ofNullable(parameter.getParameterAnnotation(SearchSpec.class)).orElseThrow(
                () -> new IllegalRequestHeadersException("search param not found"));
        return buildSpecification(webRequest.getParameter(def.searchParam()));
    }

    protected <T> Specification<T> buildSpecification(String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }
        SpecificationsBuilder<T> specBuilder = new SpecificationsBuilder<>();
        return specBuilder.withSearch(search).build();
    }

}
