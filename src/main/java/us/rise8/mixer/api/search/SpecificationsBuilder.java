package us.rise8.mixer.api.search;

import org.springframework.data.jpa.domain.Specification;

public class SpecificationsBuilder<U> {

    private final CriteriaParser<U> parser = new CriteriaParser<>();
    private Specification<U> specs = new NullSpecification<>();

    public SpecificationsBuilder<U> withSearch(String search) {
        specs = parser.parse(search);
        return this;
    }

    public Specification<U> build() {
        return specs;
    }

}
