package mil.af.abms.midas.api.assertion;

import org.springframework.data.jpa.domain.Specification;

import mil.af.abms.midas.enums.ProgressionStatus;

public class AssertionSpecifications {

    private AssertionSpecifications() {
        throw new IllegalStateException("Utility Class");
    }

    public static Specification<Assertion> hasProductId(Long id) {
        return (root, query, cb) -> cb.equal(root.get("product").get("id"), id);
    }

    public static Specification<Assertion> hasStatus(ProgressionStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
}
