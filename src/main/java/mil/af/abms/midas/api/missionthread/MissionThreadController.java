package mil.af.abms.midas.api.missionthread;


import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.missionthread.dto.CreateMissionThreadDTO;
import mil.af.abms.midas.api.missionthread.dto.MissionThreadDTO;
import mil.af.abms.midas.api.missionthread.dto.UpdateMissionThreadDTO;

@RestController
@RequestMapping("/api/missionthreads")
public class MissionThreadController extends AbstractCRUDController<MissionThread, MissionThreadDTO, MissionThreadService> {

    public MissionThreadController(MissionThreadService service) { super(service); }

    @PostMapping
    public MissionThreadDTO create(@Valid @RequestBody CreateMissionThreadDTO createMissionThreadDTO) {
        return service.create(createMissionThreadDTO).toDto();
    }

    @PutMapping("/{id}")
    public MissionThreadDTO updateById(@Valid @RequestBody UpdateMissionThreadDTO updateMissionThreadDTO, @PathVariable Long id) {
        return service.updateById(id, updateMissionThreadDTO).toDto();
    }

    @PutMapping("/{id}/archive")
    public MissionThreadDTO updateIsArchived(@Valid @RequestBody IsArchivedDTO isArchivedDTO, @PathVariable Long id) {
        return service.updateIsArchived(id, isArchivedDTO).toDto();
    }

}
