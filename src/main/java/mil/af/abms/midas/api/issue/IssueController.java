package mil.af.abms.midas.api.issue;

import javax.validation.Valid;

import java.time.LocalDate;
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
import mil.af.abms.midas.api.dtos.AddGitLabIssueWithProductDTO;
import mil.af.abms.midas.api.issue.dto.IssueDTO;

@RestController
@RequestMapping("/api/issues")
public class IssueController extends AbstractCRUDController<Issue, IssueDTO, IssueService> {

    @Autowired
    public IssueController(IssueService service) {
        super(service);
    }

    @PostMapping
    public IssueDTO create(@Valid @RequestBody AddGitLabIssueWithProductDTO addGitLabIssueWithProductDTO) {
        return service.create(addGitLabIssueWithProductDTO).toDto();
    }

    @GetMapping("/sync/{id}")
    public IssueDTO syncById(@PathVariable Long id) {
        return service.updateById(id).toDto();
    }

    @GetMapping("/sync/project/{projectId}")
    public List<IssueDTO> syncAllIssuesForProject(@PathVariable Long projectId) {
        return service.syncGitlabIssueForProject(projectId).stream().map(Issue::toDto).collect(Collectors.toList());
    }

    @GetMapping("/project/{projectId}")
    public List<IssueDTO> getAllIssuesForProject(@PathVariable Long projectId) {
        return service.getAllIssuesByProjectId(projectId).stream().map(Issue::toDto).collect(Collectors.toList());
    }

    @GetMapping("/sync/product/{productId}")
    public List<IssueDTO> syncAllIssuesForProduct(@PathVariable Long productId) {
        return service.syncGitlabIssueForProduct(productId).stream().map(Issue::toDto).collect(Collectors.toList());
    }

    @GetMapping("/product/{productId}")
    public List<IssueDTO> getAllIssuesForProduct(@PathVariable Long productId) {
        return service.getAllIssuesByProductId(productId).stream().map(Issue::toDto).collect(Collectors.toList());
    }

    @GetMapping("/portfolio/{portfolioId}/{startDate}/{endDate}")
    public List<IssueDTO> getAllIssuesForPortfolioAndDateRange(@PathVariable Long portfolioId, @PathVariable String startDate, @PathVariable String endDate) {
        return service.getAllIssuesByPortfolioIdAndDateRange(portfolioId, LocalDate.parse(startDate), LocalDate.parse(endDate)).stream().map(Issue::toDto).collect(Collectors.toList());
    }
}
