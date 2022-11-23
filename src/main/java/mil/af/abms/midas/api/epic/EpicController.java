package mil.af.abms.midas.api.epic;

import javax.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.AddGitLabEpicWithPortfolioDTO;
import mil.af.abms.midas.api.dtos.AddGitLabEpicWithProductDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.config.security.annotations.HasEpicHideAccess;

@RestController
@RequestMapping("/api/epics")
public class EpicController extends AbstractCRUDController<Epic, EpicDTO, EpicService> {

    @Autowired
    public EpicController(EpicService service) {
        super(service);
    }

    @PostMapping("/product")
    public EpicDTO createForProduct(@Valid @RequestBody AddGitLabEpicWithProductDTO addGitLabEpicWithProductDTO) {
        return service.createOrUpdateForProduct(addGitLabEpicWithProductDTO).toDto();
    }

    @PostMapping("/portfolio")
    public EpicDTO createForPortfolio(@Valid @RequestBody AddGitLabEpicWithPortfolioDTO addGitLabEpicWithPortfolioDTO) {
        return service.createOrUpdateForPortfolio(addGitLabEpicWithPortfolioDTO).toDto();
    }

    @GetMapping("/sync/product/{id}")
    public EpicDTO syncByIdForProduct(@PathVariable Long id) {
        return service.updateByIdForProduct(id).toDto();
    }

    @GetMapping("/sync/portfolio/{id}")
    public EpicDTO syncByIdForPortfolio(@PathVariable Long id) {
        return service.updateByIdForPortfolio(id).toDto();
    }

    @GetMapping("/last-synced/product/{id}")
    public LocalDateTime getLastSyncedAtForProduct(@PathVariable Long id) {
        return service.getLastSyncedAtForProduct(id);
    }

    @GetMapping("/last-synced/portfolio/{id}")
    public LocalDateTime getLastSyncedAtForPortfolio(@PathVariable Long id) {
        return service.getLastSyncedAtForPortfolio(id);
    }

    @GetMapping("/all/product/{productId}")
    public List<EpicDTO> getAllGroupEpicsForProducts(@PathVariable Long productId) {
        var product = service.getProductById(productId);
        return service.gitlabEpicSync(product).stream().map(Epic::toDto).collect(Collectors.toList());
    }

    @GetMapping("/all/portfolio/{portfolioId}")
    public List<EpicDTO> getAllGroupEpicsForPortfolios(@PathVariable Long portfolioId) {
        var portfolio = service.getPortfolioById(portfolioId);
        return service.gitlabEpicSync(portfolio).stream().map(Epic::toDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}/hide")
    @HasEpicHideAccess
    public EpicDTO updateIsHidden(@Valid @RequestBody IsHiddenDTO isHiddenDTO, @PathVariable Long id) {
        return service.updateIsHidden(id, isHiddenDTO).toDto();
    }

}
