package mil.af.abms.midas.api.persona;

import javax.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.persona.dto.CreatePersonaDTO;
import mil.af.abms.midas.api.persona.dto.PersonaDTO;
import mil.af.abms.midas.api.persona.dto.UpdatePersonaDTO;
import mil.af.abms.midas.api.product.ProductService;

@Service
public class PersonaService extends AbstractCRUDService<Persona, PersonaDTO, PersonaRepository> {

    private ProductService productService;

    public PersonaService(PersonaRepository repository) {
        super(repository, Persona.class, PersonaDTO.class);
    }

    @Autowired
    public void setUserService(ProductService productService) { this.productService = productService; }


    @Transactional
    public List<Persona> bulkUpdate(List<UpdatePersonaDTO> dtos) {
        return dtos.stream().map(r -> updateById(r.getId(), r)).collect(Collectors.toList());
    }
    @Transactional
    public Persona create(CreatePersonaDTO dto) {
        Persona newPersona = Builder.build(Persona.class)
                .with(p -> p.setTitle(dto.getTitle()))
                .with(p -> p.setDescription(dto.getDescription()))
                .with(p -> p.setIsSupported(dto.getIsSupported()))
                .with(p -> p.setPosition(dto.getIndex()))
                .with(p -> p.setProduct(productService.findById(dto.getProductId())))
                .get();

        return repository.save(newPersona);
    }

    @Transactional
    public Persona updateById(Long id, UpdatePersonaDTO dto) {
        Persona foundPersona = findById(id);
        foundPersona.setTitle(dto.getTitle());
        foundPersona.setDescription(dto.getDescription());
        foundPersona.setPosition(dto.getIndex());
        foundPersona.setIsSupported(dto.getIsSupported());

        return repository.save(foundPersona);
    }

}
