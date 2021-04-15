package mil.af.abms.midas.api.tag;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.tag.dto.CreateTagDTO;
import mil.af.abms.midas.api.tag.dto.TagDTO;
import mil.af.abms.midas.api.tag.dto.UpdateTagDTO;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class TagService extends AbstractCRUDService<Tag, TagDTO, TagRepository> {

    UserService userService;

    @Autowired
    public TagService(TagRepository repository) {
        super(repository, Tag.class, TagDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }

    @Transactional
    public Tag create(CreateTagDTO createTagDTO) {
        Tag newTag = Builder.build(Tag.class)
                .with(t -> t.setLabel(createTagDTO.getLabel()))
                .with(t -> t.setDescription(createTagDTO.getDescription()))
                .with(t -> t.setCreatedBy(userService.getUserBySecContext()))
                .with(t -> t.setColor(createTagDTO.getColor())).get();

        return repository.save(newTag);
    }

    @Transactional
    public Tag findByLabel(String label) {
        return repository.findByLabel(label).orElseThrow(
                () -> new EntityNotFoundException(Tag.class.getSimpleName(), "label", label));
    }

    @Transactional
    public Tag updateById(Long id, UpdateTagDTO updateTagDTO) {
        Tag foundTag = getObject(id);

        foundTag.setLabel(updateTagDTO.getLabel());
        foundTag.setDescription(updateTagDTO.getDescription());
        foundTag.setColor(updateTagDTO.getColor());

        return repository.save(foundTag);
    }

}
