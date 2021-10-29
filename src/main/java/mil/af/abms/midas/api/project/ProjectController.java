package mil.af.abms.midas.api.project;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.project.dto.ArchiveProjectDTO;
import mil.af.abms.midas.api.project.dto.CreateProjectDTO;
import mil.af.abms.midas.api.project.dto.ProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectJourneyMapDTO;
import mil.af.abms.midas.clients.gitlab.models.GitLabProject;
import mil.af.abms.midas.config.security.annotations.HasProjectAccess;

@RestController
@RequestMapping("/api/projects")
public class ProjectController extends AbstractCRUDController<Project, ProjectDTO, ProjectService> {

    @Autowired
    public ProjectController(ProjectService service) { super(service); }

    @PostMapping
    public ProjectDTO create(@Valid @RequestBody CreateProjectDTO createProjectDTO) {
        return service.create(createProjectDTO).toDto();
    }

    @PostMapping("/from_gitlab")
    public ProjectDTO createFromGitlab(@Valid @RequestBody GitLabProject gitLabProject) {
        return service.createFromGitlab(gitLabProject).toDto();
    }

    @GetMapping("/sync/{id}")
    public ProjectDTO syncById(@PathVariable Long id) {
        return service.syncProjectWithGitlab(id).toDto();
    }

    @HasProjectAccess
    @PutMapping("/{id}")
    public ProjectDTO updateById(@Valid @RequestBody UpdateProjectDTO updateProjectDTO, @PathVariable Long id) {
        return service.updateById(id, updateProjectDTO).toDto();
    }

    @HasProjectAccess
    @PutMapping("/{id}/journeymap")
    public ProjectDTO updateProjectJourneyMapById(@RequestBody UpdateProjectJourneyMapDTO updateProjectJourneyMapDTO, @PathVariable Long id) {
        return service.updateJourneyMapById(id, updateProjectJourneyMapDTO).toDto();
    }

    @HasProjectAccess
    @PutMapping("/{id}/archive")
    public ProjectDTO archiveById(@RequestBody ArchiveProjectDTO archiveProjectDTO, @PathVariable Long id) {
        return service.archive(id, archiveProjectDTO).toDto();
    }

}
