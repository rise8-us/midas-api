package mil.af.abms.midas.api.release;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.release.dto.ReleaseDTO;

@RestController
@RequestMapping("/api/releases")
public class ReleaseController extends AbstractCRUDController<Release, ReleaseDTO, ReleaseService> {

    public ReleaseController(ReleaseService service) { super(service); }

    @GetMapping("/sync/project/{projectId}")
    public List<ReleaseDTO> syncAllReleasesForProject(@PathVariable Long projectId) {
        return service.syncGitlabReleaseForProject(projectId).stream().map(Release::toDto).collect(Collectors.toList());
    }

    @GetMapping("/project/{projectId}")
    public List<ReleaseDTO> getAllReleasesForProject(@PathVariable Long projectId) {
        return service.getAllReleasesByProjectId(projectId).stream().map(Release::toDto).collect(Collectors.toList());
    }

    @GetMapping("/sync/product/{productId}")
    public List<ReleaseDTO> syncAllReleasesForProduct(@PathVariable Long productId) {
        return service.syncGitlabReleaseForProduct(productId).stream().map(Release::toDto).collect(Collectors.toList());
    }

    @GetMapping("/product/{productId}")
    public List<ReleaseDTO> getAllReleasesForProduct(@PathVariable Long productId) {
        return service.getAllReleasesByProductId(productId).stream().map(Release::toDto).collect(Collectors.toList());
    }

}
