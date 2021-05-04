package mil.af.abms.midas.api.assertion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.ogsm.OgsmService;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.AssertionType;

@WebMvcTest({AssertionController.class})
public class AssertionControllerTests extends ControllerTestHarness {
    
    @MockBean
    private AssertionService assertionService;
    @MockBean
    private TagService tagService;
    @MockBean
    private OgsmService ogsmService;
    
    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Tag tags = Builder.build(Tag.class).with(t -> t.setId(2L)).get();
    private final Set<Comment> comments = Set.of(Builder.build(Comment.class).with(c -> c.setId(2L)).get());
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("First"))
            .with(a -> a.setType(AssertionType.OBJECTIVE))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setComments(comments))
            .with(a -> a.setCreatedBy(createdBy)).get();
    CreateAssertionDTO createAssertionDTO = new CreateAssertionDTO("First", AssertionType.OBJECTIVE,  1L, Set.of(2L), null, Set.of());
    UpdateAssertionDTO updateAssertionDTO = new UpdateAssertionDTO("updated", AssertionType.MEASURE, Set.of(2L),Set.of(2L), null, Set.of());

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_assertion() throws Exception {
        when(assertionService.create(any(CreateAssertionDTO.class))).thenReturn(assertion);
        when(ogsmService.existsById(anyLong())).thenReturn(true);
        when(tagService.existsById(2L)).thenReturn(true);

        mockMvc.perform(post("/api/assertions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createAssertionDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value("First"));
    }

    @Test
    public void should_update_by_id() throws Exception {
        when(assertionService.updateById(any(), any(UpdateAssertionDTO.class))).thenReturn(assertion);
        when(tagService.existsById(2L)).thenReturn(true);

        mockMvc.perform(put("/api/assertions/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateAssertionDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value("First"));
    }

    @Test
    public void should_throw_type_must_not_be_null_message_on_create() throws Exception {
        CreateAssertionDTO createDTONullType = new CreateAssertionDTO("First", null,  1L, Set.of(2L), null, Set.of());
        Assertion assertionNullType = new Assertion();
        BeanUtils.copyProperties(assertion, assertionNullType);
        assertionNullType.setType(null);

        when(assertionService.create(any(CreateAssertionDTO.class))).thenReturn(assertion);
        when(ogsmService.existsById(anyLong())).thenReturn(true);
        when(tagService.existsById(2L)).thenReturn(true);

        mockMvc.perform(put("/api/assertions/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createDTONullType))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value("type must not be blank"));
    }

    @Test
    public void should_throw_type_must_not_be_null_message_on_update() throws Exception {
        UpdateAssertionDTO updateDTONullType = new UpdateAssertionDTO("updated", null, Set.of(2L), Set.of(2L), null, Set.of());
        Assertion assertionNullType = new Assertion();
        BeanUtils.copyProperties(assertion, assertionNullType);
        assertionNullType.setType(null);

        when(assertionService.updateById(any(), any(UpdateAssertionDTO.class))).thenReturn(assertion);
        when(tagService.existsById(2L)).thenReturn(true);

        mockMvc.perform(put("/api/assertions/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateDTONullType))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value("type must not be blank"));
    }
    
}
