package mil.af.abms.midas.api.performancemeasure;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.performancemeasure.dto.CreatePerformanceMeasureDTO;
import mil.af.abms.midas.api.performancemeasure.dto.PerformanceMeasureDTO;
import mil.af.abms.midas.api.performancemeasure.dto.UpdatePerformanceMeasureDTO;

@RestController
@RequestMapping("/api/performancemeasures")
public class PerformanceMeasureController extends AbstractCRUDController<PerformanceMeasure, PerformanceMeasureDTO, PerformanceMeasureService> {

    public PerformanceMeasureController(PerformanceMeasureService service) { super(service); }

    @PostMapping
    public PerformanceMeasureDTO create(@Valid @RequestBody CreatePerformanceMeasureDTO createPerformanceMeasureDTO) {
        return service.create(createPerformanceMeasureDTO).toDto();
    }

    @PutMapping("/{id}")
    public PerformanceMeasureDTO updateById(@Valid @RequestBody UpdatePerformanceMeasureDTO updatePerformanceMeasureDTO, @PathVariable Long id) {
        return service.updateById(id, updatePerformanceMeasureDTO).toDto();
    }

    @PutMapping("/bulk")
    public List<PerformanceMeasureDTO> bulkUpdate(@Valid @RequestBody List<UpdatePerformanceMeasureDTO> updatePerformanceMeasureDTOs) {
        return service.bulkUpdate(updatePerformanceMeasureDTOs).stream().map(PerformanceMeasure::toDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}/archive")
    public PerformanceMeasureDTO updateIsArchived(@Valid @RequestBody IsArchivedDTO isArchivedDTO, @PathVariable Long id) {
        return service.updateIsArchived(id, isArchivedDTO).toDto();
    }

}
