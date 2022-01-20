package mil.af.abms.midas.api.feature;

import javax.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.feature.dto.CreateFeatureDTO;
import mil.af.abms.midas.api.feature.dto.FeatureDTO;
import mil.af.abms.midas.api.feature.dto.UpdateFeatureDTO;
import mil.af.abms.midas.config.security.annotations.HasFeatureAccess;

@RestController
@RequestMapping("/api/features")
public class FeatureController extends AbstractCRUDController<Feature, FeatureDTO, FeatureService> {

    @Autowired
    public FeatureController(FeatureService service) {
        super(service);
    }

    @HasFeatureAccess
    @PostMapping
    public FeatureDTO create(@Valid @RequestBody CreateFeatureDTO createFeatureDTODTO) {
        return service.create(createFeatureDTODTO).toDto();
    }

    @HasFeatureAccess
    @PutMapping("/{id}")
    public FeatureDTO updateById(@Valid @RequestBody UpdateFeatureDTO updateFeatureDTO, @PathVariable Long id) {
        return service.updateById(id, updateFeatureDTO).toDto();
    }

    @HasFeatureAccess
    @PutMapping("/bulk")
    public List<FeatureDTO> bulkUpdate(@Valid @RequestBody List<UpdateFeatureDTO> updateFeatureDTOs) {
        return service.bulkUpdate(updateFeatureDTOs).stream().map(Feature::toDto).collect(Collectors.toList());
    }

    @Override
    @HasFeatureAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
