package mil.af.abms.midas.api.measure;


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
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.measure.dto.MeasureDTO;
import mil.af.abms.midas.api.measure.dto.UpdateMeasureDTO;
import mil.af.abms.midas.config.security.annotations.HasMeasureCreateAccess;
import mil.af.abms.midas.config.security.annotations.HasMeasureUpdateAccess;

@RestController
@RequestMapping("/api/measures")
public class MeasureController extends AbstractCRUDController<Measure, MeasureDTO, MeasureService> {

    @Autowired
    public MeasureController(MeasureService service) { super(service); }

    @HasMeasureCreateAccess
    @PostMapping
    public MeasureDTO create(@Valid @RequestBody CreateMeasureDTO createMeasuresDTO) {
        return service.create(createMeasuresDTO).toDto();
    }

    @HasMeasureUpdateAccess
    @PutMapping("/{id}")
    public MeasureDTO updateById(@Valid @RequestBody UpdateMeasureDTO updateMeasuresDTO, @PathVariable Long id) {
        return service.updateById(id, updateMeasuresDTO).toDto();
    }

    @Override
    @HasMeasureUpdateAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }

}
