package mil.af.abms.midas.api.portfolio;

import javax.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.dtos.SprintProductMetricsDTO;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.config.security.annotations.HasPortfolioAccess;
import mil.af.abms.midas.config.security.annotations.IsPortfolioLeadershipOrAdmin;

@RestController
@RequestMapping("/api/portfolios")
public class PortfolioController extends AbstractCRUDController<Portfolio, PortfolioDTO, PortfolioService> {

    @Autowired
    public PortfolioController(PortfolioService service) {
        super(service);
    }

    @IsPortfolioLeadershipOrAdmin
    @PostMapping
    public PortfolioDTO create(@Valid @RequestBody CreatePortfolioDTO createPortfolioDTO) {
        return service.create(createPortfolioDTO).toDto();
    }

    @HasPortfolioAccess
    @PutMapping("/{id}")
    public PortfolioDTO updateById(@Valid @RequestBody UpdatePortfolioDTO updatePortfolioDTO, @PathVariable Long id) {
        return service.updateById(id, updatePortfolioDTO).toDto();
    }

    @HasPortfolioAccess
    @PutMapping("/{id}/archive")
    public PortfolioDTO updateIsArchivedById(@RequestBody IsArchivedDTO isArchivedDTO, @PathVariable Long id) {
        return service.updateIsArchivedById(id, isArchivedDTO).toDto();
    }

    @GetMapping("/{id}/sprint-metrics/{startDate}")
    public TreeMap<LocalDate, List<SprintProductMetricsDTO>> getSprintMetrics(@PathVariable Long id, @PathVariable String startDate, @RequestParam(defaultValue = "14") Integer duration, @RequestParam(defaultValue = "10") Integer sprints) {
        return service.getSprintMetrics(id, LocalDate.parse(startDate), duration, sprints);
    }
}
