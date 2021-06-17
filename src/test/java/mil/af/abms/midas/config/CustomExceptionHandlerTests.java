package mil.af.abms.midas.config;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.tag.TagController;
import mil.af.abms.midas.api.tag.TagRepository;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.tag.dto.CreateTagDTO;
import mil.af.abms.midas.enums.TagType;
import mil.af.abms.midas.exception.EntityNotFoundException;

@WebMvcTest({TagController.class})
class CustomExceptionHandlerTests extends ControllerTestHarness {


    @SpyBean
    TagController controller;
    @MockBean
    private TagService tagService;
    @MockBean
    private TagRepository tagRepository;

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void should_create_tag() throws Exception {
        CreateTagDTO createTagDTO = new CreateTagDTO("tag test", "New Tag", "#969969", TagType.PRODUCT);
        when(tagService.findByLabel("tag test")).thenThrow(EntityNotFoundException.class);
        when(userService.existsById(anyLong())).thenReturn(true);
        doThrow(new HttpMessageNotReadableException("foobar", new MockHttpInputMessage("foo".getBytes(StandardCharsets.UTF_8))))
                .when(controller).create(any());

        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(createTagDTO))
        )
                .andExpect(status().is4xxClientError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("foobar"));
    }
}
