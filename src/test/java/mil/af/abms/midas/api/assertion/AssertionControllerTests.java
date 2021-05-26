package mil.af.abms.midas.api.assertion;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.enums.AssertionStatus;
import mil.af.abms.midas.enums.AssertionType;

@WebMvcTest({AssertionController.class})
public class AssertionControllerTests extends ControllerTestHarness {
    
    @MockBean
    private AssertionService assertionService;
    @MockBean
    private ProductService productService;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Set<Comment> comments = Set.of(Builder.build(Comment.class).with(c -> c.setId(2L)).get());
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(3L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("First"))
            .with(a -> a.setType(AssertionType.OBJECTIVE))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setComments(comments))
            .with(a -> a.setCreatedBy(createdBy)).get();
    CreateAssertionDTO createAssertionDTO = new CreateAssertionDTO("First", AssertionType.GOAL, 1L, null, null, new ArrayList<>());
    UpdateAssertionDTO updateAssertionDTO = new UpdateAssertionDTO("updated", AssertionStatus.NOT_STARTED, List.of());

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_assertion() throws Exception {
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

    @Test
    public void should_throw_type_must_not_be_null_message_on_create() throws Exception {
        CreateAssertionDTO createDTONullType = new CreateAssertionDTO("First",null,
                1L, null, null, new ArrayList<>());

        when(productService.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/assertions")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createDTONullType))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.errors[0]").value("type must not be blank"));
    }

    @Test
    public void should_throw_text_must_not_be_null_message_on_update() throws Exception {
        UpdateAssertionDTO updateDTONullType = new UpdateAssertionDTO("", AssertionStatus.NOT_STARTED, List.of());
        Assertion assertionNullType = new Assertion();
        BeanUtils.copyProperties(assertion, assertionNullType);
        assertionNullType.setType(null);

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
    public void should_delete_by_id() throws Exception {
        mockMvc.perform(delete("/api/assertions/1"))
                .andExpect(status().isOk());

        verify(assertionService, times(1)).deleteById(1L);
    }
    
}
