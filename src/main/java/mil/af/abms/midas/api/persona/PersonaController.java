package mil.af.abms.midas.api.persona;

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
import mil.af.abms.midas.api.persona.dto.CreatePersonaDTO;
import mil.af.abms.midas.api.persona.dto.PersonaDTO;
import mil.af.abms.midas.api.persona.dto.UpdatePersonaDTO;
import mil.af.abms.midas.config.security.annotations.HasPersonaAccess;

@RestController
@RequestMapping("/api/personas")
public class PersonaController extends AbstractCRUDController<Persona, PersonaDTO, PersonaService> {

    @Autowired
    public PersonaController(PersonaService service) {
        super(service);
    }

    @HasPersonaAccess
    @PostMapping
    public PersonaDTO create(@Valid @RequestBody CreatePersonaDTO createPersonaDTO) {
        return service.create(createPersonaDTO).toDto();
    }

    @HasPersonaAccess
    @PutMapping("/{id}")
    public PersonaDTO updateById(@Valid @RequestBody UpdatePersonaDTO updatePersonaDTO, @PathVariable Long id) {
        return service.updateById(id, updatePersonaDTO).toDto();
    }

    @HasPersonaAccess
    @PutMapping("/bulk")
    public List<PersonaDTO> bulkUpdate(@Valid @RequestBody List<UpdatePersonaDTO> updatePersonaDTOs) {
        return service.bulkUpdate(updatePersonaDTOs).stream().map(Persona::toDto).collect(Collectors.toList());
    }

    @Override
    @HasPersonaAccess
    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id) {
        service.deleteById(id);
    }
}
