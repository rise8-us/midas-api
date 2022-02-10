package mil.af.abms.midas.api.issue;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.AddGitLabIssueDTO;
import mil.af.abms.midas.api.issue.dto.IssueDTO;

@RestController
@RequestMapping("/api/issues")
public class IssueController extends AbstractCRUDController<Issue, IssueDTO, IssueService> {

    @Autowired
    public IssueController(IssueService service) {
        super(service);
    }

    @PostMapping
    public IssueDTO create(@Valid @RequestBody AddGitLabIssueDTO addGitLabIssueDTO) {
        return service.create(addGitLabIssueDTO).toDto();
    }

    @GetMapping("/sync/{id}")
    public IssueDTO syncById(@PathVariable Long id) {
        return service.updateById(id).toDto();
    }

    @GetMapping("/all/{projectId}")
    public List<IssueDTO> getAllIssuesForProject(@PathVariable Long projectId) {
        return service.getAllGitlabIssuesForProject(projectId).stream().map(Issue::toDto).collect(Collectors.toList());
    }

}
