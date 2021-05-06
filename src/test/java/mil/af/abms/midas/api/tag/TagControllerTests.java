package mil.af.abms.midas.api.tag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.tag.dto.CreateTagDTO;
import mil.af.abms.midas.api.tag.dto.UpdateTagDTO;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({TagController.class})
public class TagControllerTests extends ControllerTestHarness {

    @MockBean
    private TagService tagService;
    @MockBean
    private TagRepository tagRepository;

    private final User user = Builder.build(User.class)
            .with(u -> u.setId(1L)).get();
    private final Tag tag = Builder.build(Tag.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setLabel("tag test"))
            .with(t -> t.setDescription("New Tag"))
            .with(t -> t.setCreatedBy(user))
            .with(t -> t.setColor("#969696")).get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_tag() throws Exception {
        CreateTagDTO createTagDTO = new CreateTagDTO("tag test", "New Tag", "#969969");

        when(tagService.findByLabel("tag test")).thenThrow(EntityNotFoundException.class);
        when(userService.existsById(anyLong())).thenReturn(true);
        when(tagService.create(any(CreateTagDTO.class))).thenReturn(tag);

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createTagDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.label").value(tag.getLabel()));
    }

    @Test
    public void should_update_tag_by_id() throws Exception {
        UpdateTagDTO updateTagDTO = new UpdateTagDTO("Tag Update", "Tag description", "#969696");
        Tag updateTag = new Tag();
        BeanUtils.copyProperties(tag, updateTag);
        updateTag.setLabel(updateTagDTO.getLabel());

        when(tagService.findByLabel(updateTag.getLabel())).thenReturn(tag);
        when(tagService.updateById(anyLong(), any(UpdateTagDTO.class))).thenReturn(updateTag);

        mockMvc.perform(put("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateTagDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.label").value(updateTag.getLabel()));
    }

}
