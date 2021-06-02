package mil.af.abms.midas.api.project;

import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {

    public static Specification<Project> hasGitlabProjectId() {
        return (root, query, cb) -> cb.isNotNull(root.get("gitlabProjectId"));
    }

}
