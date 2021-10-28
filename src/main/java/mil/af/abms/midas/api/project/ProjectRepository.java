package mil.af.abms.midas.api.project;

import java.util.Optional;

import mil.af.abms.midas.api.RepositoryInterface;
import mil.af.abms.midas.api.project.dto.ProjectDTO;

public interface ProjectRepository extends RepositoryInterface<Project, ProjectDTO> {

    Optional<Project> findByName(String name);

    Optional<Project> findByGitlabProjectId(Integer gitlabProjectId);

}
