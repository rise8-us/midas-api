package mil.af.abms.midas.api.tag;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.tag.dto.CreateTagDTO;
import mil.af.abms.midas.api.tag.dto.UpdateTagDTO;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.TagType;
import mil.af.abms.midas.exception.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
@Import(TagService.class)
public class TagServiceTests {
    
    @Autowired
    TagService tagService;
    @MockBean
    TagRepository tagRepository;
    @MockBean
    UserService userService;

    @Captor
    ArgumentCaptor<Tag> tagCaptor;

    private final Project project = Builder.build(Project.class)
            .with(p -> p.setId(3L))
            .with(p -> p.setName("Project"))
            .with(p -> p.setGitlabProjectId(2)).get();
    private final Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setLabel("tag test"))
            .with(t -> t.setDescription("New Tag"))
            .with(t -> t.setColor("#9699696"))
            .with(t -> t.setProjects(Set.of(project))).get();

    @Test
    public void should_create_tag() {
        CreateTagDTO createTagDTO = new CreateTagDTO("Tag Label", "Test Desc", "#969696", TagType.PRODUCT);

        when(tagRepository.save(tag)).thenReturn(new Tag());

        tagService.create(createTagDTO);

        verify(tagRepository, times(1)).save(tagCaptor.capture());
        Tag tagSaved = tagCaptor.getValue();

        assertThat(tagSaved.getLabel()).isEqualTo(createTagDTO.getLabel());
        assertThat(tagSaved.getDescription()).isEqualTo(createTagDTO.getDescription());
        assertThat(tagSaved.getColor()).isEqualTo(createTagDTO.getColor());
    }

    @Test
    public void should_find_by_label() throws EntityNotFoundException {
        when(tagRepository.findByLabel(tag.getLabel())).thenReturn(Optional.of(tag));

        assertThat(tagService.findByLabel(tag.getLabel())).isEqualTo(tag);
    }

    @Test
    public void should_throw_error_find_by_label() throws EntityNotFoundException {
        assertThrows(EntityNotFoundException.class, () ->
                tagService.findByLabel(tag.getLabel()));
    }

    @Test
    public void should_update_project_by_id() {
        UpdateTagDTO updateTagDTO = new UpdateTagDTO("new tag", "tag test", "#969696", TagType.PRODUCT);
        Project newProject = new Project();
        BeanUtils.copyProperties(project, newProject);
        newProject.setId(3L);

        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(tag);

        tagService.updateById(1L, updateTagDTO);

        verify(tagRepository, times(1)).save(tagCaptor.capture());
        Tag tagSaved = tagCaptor.getValue();

        assertThat(tagSaved.getLabel()).isEqualTo(updateTagDTO.getLabel());
        assertThat(tagSaved.getDescription()).isEqualTo(updateTagDTO.getDescription());
        assertThat(tagSaved.getColor()).isEqualTo(updateTagDTO.getColor());
    }

}
