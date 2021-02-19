package us.rise8.mixer.api.search.configuration;

import org.springframework.core.MethodParameter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import us.rise8.mixer.api.search.SpecificationsBuilder;
import us.rise8.mixer.api.search.annotation.SearchSpec;

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
    ) throws Exception {
        SearchSpec def = parameter.getParameterAnnotation(SearchSpec.class);
        return buildSpecification(webRequest.getParameter(def.searchParam()));
    }

    private <T> Specification<T> buildSpecification(String search) {
        if (search == null || search.isEmpty()) {
            return null;
        }
        SpecificationsBuilder<T> specBuilder = new SpecificationsBuilder<>();
        return specBuilder.withSearch(search).build();
    }

}
