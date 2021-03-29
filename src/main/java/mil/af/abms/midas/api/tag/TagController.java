package mil.af.abms.midas.api.tag;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mil.af.abms.midas.api.AbstractCRUDController;
import mil.af.abms.midas.api.tag.dto.CreateTagDTO;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.api.tag.dto.UpdateTagDTO;

@RestController
@RequestMapping("/api/tags")
public class TagController extends AbstractCRUDController<Tag, TagDTO, TagService> {

    @Autowired
    public TagController(TagService service) { super(service); }

    @PostMapping
    public TagDTO create(@Valid @RequestBody CreateTagDTO createTagDTO) {
        return service.create(createTagDTO).toDto();
    }

    @PutMapping("/{id}")
    public TagDTO updateById(@Valid @RequestBody UpdateTagDTO updateTagDTO, @PathVariable Long id) {

        return service.updateById(id, updateTagDTO).toDto();
    }

}
