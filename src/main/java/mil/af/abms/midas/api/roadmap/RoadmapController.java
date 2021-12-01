package mil.af.abms.midas.api.roadmap;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.roadmap.dto.CreateRoadmapDTO;
import mil.af.abms.midas.api.roadmap.dto.RoadmapDTO;
import mil.af.abms.midas.api.roadmap.dto.UpdateRoadmapDTO;

@RestController
@RequestMapping("/api/roadmaps")
public class RoadmapController extends AbstractCRUDController<Roadmap, RoadmapDTO, RoadmapService> {

    @Autowired
    public RoadmapController(RoadmapService service) {
        super(service);
    }

    @PostMapping
    public RoadmapDTO create(@Valid @RequestBody CreateRoadmapDTO roadmapDTO) {
        return service.create(roadmapDTO).toDto();
    }

    @PutMapping("/{id}")
    public RoadmapDTO updateById(@Valid @RequestBody UpdateRoadmapDTO updateRoadmapDTO, @PathVariable Long id) {
        return service.updateById(id, updateRoadmapDTO).toDto();
    }

    @PutMapping("/bulk")
    public List<RoadmapDTO> bulkUpdate(@Valid @RequestBody List<UpdateRoadmapDTO> updateRoadmapDTOs) {
        return service.bulkUpdate(updateRoadmapDTOs).stream().map(Roadmap::toDto).collect(Collectors.toList());
    }

    @PutMapping("/{id}/hide")
    public RoadmapDTO updateIsHidden(@Valid @RequestBody IsHiddenDTO isHiddenDTO, @PathVariable Long id) {
        return service.updateIsHidden(id, isHiddenDTO).toDto();
    }

}
