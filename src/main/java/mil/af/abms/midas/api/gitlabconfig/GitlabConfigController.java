package mil.af.abms.midas.api.gitlabconfig;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.gitlabconfig.dto.CreateUpdateGitlabConfigDTO;
import mil.af.abms.midas.api.gitlabconfig.dto.GitlabConfigDTO;

@RestController
@RequestMapping("/api/gitlabConfigs")
public class GitlabConfigController extends AbstractCRUDController<GitlabConfig, GitlabConfigDTO, GitlabConfigService> {

    @Autowired
    public GitlabConfigController(GitlabConfigService service) {
        super(service);
    }

    @PostMapping
    public GitlabConfigDTO create(@Valid @RequestBody CreateUpdateGitlabConfigDTO cDto) {
        return service.create(cDto).toDto();
    }

    @PutMapping("/{id}")
    public GitlabConfigDTO create(@PathVariable Long id, @Valid @RequestBody CreateUpdateGitlabConfigDTO uDto) {
        return service.updateById(id, uDto).toDto();
    }

}
