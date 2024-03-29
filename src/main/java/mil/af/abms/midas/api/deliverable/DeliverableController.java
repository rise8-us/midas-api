package mil.af.abms.midas.api.deliverable;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.deliverable.dto.CreateDeliverableDTO;
import mil.af.abms.midas.api.deliverable.dto.DeliverableDTO;
import mil.af.abms.midas.api.deliverable.dto.UpdateDeliverableDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.config.security.annotations.HasPortfolioAdminOrAdmin;

@RestController
@RequestMapping("/api/deliverables")
public class DeliverableController extends AbstractCRUDController<Deliverable, DeliverableDTO, DeliverableService> {

    public DeliverableController(DeliverableService service) { super(service); }

    @HasPortfolioAdminOrAdmin
    @PostMapping
    public DeliverableDTO create(@Valid @RequestBody CreateDeliverableDTO createDeliverableDTO) {
        return service.create(createDeliverableDTO).toDto();
    }

    @HasPortfolioAdminOrAdmin
    @PutMapping("/{id}")
    public DeliverableDTO updateById(@Valid @RequestBody UpdateDeliverableDTO updateDeliverableDTO, @PathVariable Long id) {
        return service.updateById(id, updateDeliverableDTO).toDto();
    }

    @HasPortfolioAdminOrAdmin
    @PutMapping("/bulk")
    public List<DeliverableDTO> bulkUpdate(@Valid @RequestBody List<UpdateDeliverableDTO> updateDeliverableDTOs) {
        return service.bulkUpdate(updateDeliverableDTOs).stream().map(Deliverable::toDto).collect(Collectors.toList());
    }

    @HasPortfolioAdminOrAdmin
    @PutMapping("/{id}/archive")
    public DeliverableDTO updateIsArchived(@Valid @RequestBody IsArchivedDTO isArchivedDTO, @PathVariable Long id) {
        return service.updateIsArchived(id, isArchivedDTO).toDto();
    }

    @HasPortfolioAdminOrAdmin
    @Override
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }
}
