package mil.af.abms.midas.api.assertion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

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
    
    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Tag tags = Builder.build(Tag.class).with(t -> t.setId(2L)).get();
    private final Set<Comment> comments = Set.of(Builder.build(Comment.class).with(c -> c.setId(2L)).get());
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("First"))
            .with(a -> a.setType(AssertionType.OBJECTIVE))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setTags(Set.of(tags)))
            .with(a -> a.setComments(comments))
            .with(a -> a.setCreatedBy(createdBy)).get();
    CreateAssertionDTO createAssertionDTO = new CreateAssertionDTO("First", AssertionType.OBJECTIVE,  1L, Set.of(2L), Set.of(2L));
    UpdateAssertionDTO updateAssertionDTO = new UpdateAssertionDTO("updated", AssertionType.MEASURE, Set.of(2L), Set.of(2L));

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_assertion() throws Exception {
        when(assertionService.create(any(CreateAssertionDTO.class))).thenReturn(assertion);

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

        mockMvc.perform(put("/api/assertions/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateAssertionDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value("First"));
    }
    
}
