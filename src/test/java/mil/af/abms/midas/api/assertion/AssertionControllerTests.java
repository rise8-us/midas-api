package mil.af.abms.midas.api.assertion;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.assertion.dto.ArchiveAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.BlockerAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.ProgressionStatus;

@WebMvcTest({AssertionController.class})
public class AssertionControllerTests extends ControllerTestHarness {

    @MockBean
    private AssertionService assertionService;
    @MockBean
    private ProductService productService;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Set<Comment> comments = Set.of(Builder.build(Comment.class).with(c -> c.setId(2L)).get());
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("First"))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setComments(comments))
            .with(a -> a.setCreatedBy(createdBy)).get();
    CreateAssertionDTO createAssertionDTO = new CreateAssertionDTO("First", 1L, null, null, new ArrayList<>(), null, new ArrayList<>(), null, null, null, null);
    UpdateAssertionDTO updateAssertionDTO = new UpdateAssertionDTO("updated", ProgressionStatus.NOT_STARTED, List.of(), null, false, null, null);

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_assertion() throws Exception {
        when(assertionService.create(any(CreateAssertionDTO.class))).thenReturn(assertion);
        when(productService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/assertions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createAssertionDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value("First"));
    }

    @Test
    void should_update_by_id() throws Exception {
        when(assertionService.updateById(any(), any(UpdateAssertionDTO.class))).thenReturn(assertion);

        mockMvc.perform(put("/api/assertions/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateAssertionDTO))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.text").value("First"));
    }

    @Test
    void should_throw_text_must_not_be_null_message_on_update() throws Exception {
        UpdateAssertionDTO updateDTONullType = new UpdateAssertionDTO("", ProgressionStatus.NOT_STARTED, List.of(), null, false, null, null);
        Assertion assertionNullType = new Assertion();
        BeanUtils.copyProperties(assertion, assertionNullType);

        when(assertionService.updateById(any(), any(UpdateAssertionDTO.class))).thenReturn(assertion);

        mockMvc.perform(put("/api/assertions/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(updateDTONullType))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value("text must not be blank"));
    }

    @Test
    void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/assertions/1"))
                .andExpect(status().isOk());

        verify(assertionService, times(1)).deleteById(1L);
    }

    @Test
    public void should_update_assertion_is_archived_true() throws Exception {
        var assertionArchived = new Assertion();
        BeanUtils.copyProperties(assertion, assertionArchived);
        assertionArchived.setIsArchived(true);
        ArchiveAssertionDTO archiveAssertionDTO = Builder.build(ArchiveAssertionDTO.class)
                .with(d -> d.setIsArchived(true)).get();

        when(assertionService.archive(any(), any())).thenReturn(assertionArchived);

        mockMvc.perform(put("/api/assertions/1/archive")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(archiveAssertionDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(true));
    }

    @Test
    public void should_update_assertion_is_archived_false() throws Exception {
        var assertionArchived = new Assertion();
        BeanUtils.copyProperties(assertion, assertionArchived);
        assertionArchived.setIsArchived(false);
        ArchiveAssertionDTO archiveAssertionDTO = Builder.build(ArchiveAssertionDTO.class)
                .with(d -> d.setIsArchived(false)).get();

        when(assertionService.archive(any(), any())).thenReturn(assertionArchived);

        mockMvc.perform(put("/api/assertions/1/archive")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(archiveAssertionDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isArchived").value(false));
    }

    @Test
    void should_get_all_blocker_assertions() throws Exception {
        when(assertionService.getAllBlockerAssertions()).thenReturn(List.of(new BlockerAssertionDTO()));

        mockMvc.perform(get("/api/assertions/blockers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType((MediaType.APPLICATION_JSON_VALUE)))
                .andExpect(jsonPath("$", hasSize(1)));
    }

}
