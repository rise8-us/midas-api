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
import mil.af.abms.midas.config.security.annotations.HasFeatureCreateAccess;
import mil.af.abms.midas.config.security.annotations.HasFeatureUpdateAccess;

@RestController
@RequestMapping("/api/features")
public class FeatureController extends AbstractCRUDController<Feature, FeatureDTO, FeatureService> {

    @Autowired
    public FeatureController(FeatureService service) {
        super(service);
    }

    @HasFeatureCreateAccess
    @PostMapping
    public FeatureDTO create(@Valid @RequestBody CreateFeatureDTO createFeatureDTO) {
        return service.create(createFeatureDTO).toDto();
    }

    @HasFeatureUpdateAccess
    @PutMapping("/{id}")
    public FeatureDTO updateById(@Valid @RequestBody UpdateFeatureDTO updateFeatureDTO, @PathVariable Long id) {
        return service.updateById(id, updateFeatureDTO).toDto();
    }

    @HasFeatureUpdateAccess
    @PutMapping("/bulk")
    public List<FeatureDTO> bulkUpdate(@Valid @RequestBody List<UpdateFeatureDTO> updateFeatureDTOs) {
        return service.bulkUpdate(updateFeatureDTOs).stream().map(Feature::toDto).collect(Collectors.toList());
    }

    @Override
    @HasFeatureUpdateAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
