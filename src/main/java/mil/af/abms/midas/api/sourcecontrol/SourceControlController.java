package mil.af.abms.midas.api.sourcecontrol;

import javax.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.sourcecontrol.dto.CreateUpdateSourceControlDTO;
import mil.af.abms.midas.api.sourcecontrol.dto.SourceControlDTO;
import mil.af.abms.midas.clients.gitlab.models.GitLabProject;
import mil.af.abms.midas.config.security.annotations.IsAdmin;

@RestController
@RequestMapping("/api/sourceControls")
public class SourceControlController extends AbstractCRUDController<SourceControl, SourceControlDTO, SourceControlService> {

    @Autowired
    public SourceControlController(SourceControlService service) {
        super(service);
    }

    @IsAdmin
    @PostMapping
    public SourceControlDTO create(@Valid @RequestBody CreateUpdateSourceControlDTO cDto) {
        return service.create(cDto).toDto();
    }

    @IsAdmin
    @PutMapping("/{id}")
    public SourceControlDTO update(@PathVariable Long id, @Valid @RequestBody CreateUpdateSourceControlDTO uDto) {
        return service.updateById(id, uDto).toDto();
    }

    @GetMapping("/{id}/group/{gitlabGroupId}/projects")
    public List<GitLabProject> getAllGroupProjects(@PathVariable Long id, @PathVariable Integer gitlabGroupId) {
        return service.getAllGitlabProjectsForGroup(id, gitlabGroupId);
    }
}
