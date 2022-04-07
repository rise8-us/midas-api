package mil.af.abms.midas.api.gantt.milestone;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.gantt.milestone.dto.CreateMilestoneDTO;
import mil.af.abms.midas.api.gantt.milestone.dto.MilestoneDTO;
import mil.af.abms.midas.api.gantt.milestone.dto.UpdateMilestoneDTO;
import mil.af.abms.midas.config.security.annotations.HasMilestoneCreateAccess;
import mil.af.abms.midas.config.security.annotations.HasMilestoneModifyAccess;

@RestController
@RequestMapping("/api/gantt_milestones")
public class MilestoneController extends AbstractCRUDController<Milestone, MilestoneDTO, MilestoneService> {

    @Autowired
    public MilestoneController(MilestoneService service) {
        super(service);
    }

    @HasMilestoneCreateAccess
    @PostMapping
    public MilestoneDTO create(@Valid @RequestBody CreateMilestoneDTO dto) {
        return service.create(dto).toDto();
    }

    @HasMilestoneModifyAccess
    @PutMapping("/{id}")
    public MilestoneDTO updateById(@Valid @RequestBody UpdateMilestoneDTO dto, @PathVariable Long id) {
        return service.updateById(id, dto).toDto();
    }

    @Override
    @HasMilestoneModifyAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
