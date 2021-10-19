package mil.af.abms.midas.api.epic;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.dtos.AddGitLabEpicDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;

@WebMvcTest({EpicController.class})
public class EpicControllerTests extends ControllerTestHarness {

    @MockBean
    private EpicService epicService;

    @MockBean
    private ProductService productService;

    @Mock
    private GitLab4JClient gitLab4JClient;

    SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(sc -> sc.setBaseUrl("fake_url"))
            .with(sc -> sc.setToken("fake_token"))
            .get();

    Product product = Builder.build(Product.class)
            .with(p -> p.setName("name"))
            .with(p -> p.setId(2L))
            .with(p -> p.setSourceControl(sourceControl))
            .get();

    Epic epic = Builder.build(Epic.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setCreationDate(LocalDateTime.now()))
            .with(e -> e.setProduct(product))
            .with(e -> e.setTitle("title"))
            .get();

    EpicDTO epicDTO = Builder.build(EpicDTO.class)
            .with(e -> e.setId(1L))
            .with(e -> e.setCreationDate(epic.getCreationDate()))
            .with(e -> e.setProductId(product.getId()))
            .with(e -> e.setTitle("title"))
            .get();

    @BeforeEach
    void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    void throw_should_create_epic_gitLab_not_found() throws Exception {
        when(productService.findById(any())).thenReturn(product);
        when(epicService.create(any(AddGitLabEpicDTO.class))).thenReturn(epic);

        mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(new AddGitLabEpicDTO(1, 2L)))
                )
                .andExpect(status().is(400))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.message").value("Validation failed. 1 error(s)"))
                .andExpect(jsonPath("$.errors[0]").value("GitLab epic does not exist or cannot be found"));
    }

    @Test
    void should_create_epic() throws Exception {
        when(productService.findById(any())).thenReturn(product);
        when(epicService.canAddEpic(any(), any())).thenReturn(true);
        when(epicService.create(any(AddGitLabEpicDTO.class))).thenReturn(epic);

        mockMvc.perform(post("/api/epics")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(new AddGitLabEpicDTO(1, 2L)))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.productId").value(product.getId()));
    }

    @Test
    void should_update_epic_by_id() throws Exception {
        when(epicService.updateById(anyLong())).thenReturn(epic);

        mockMvc.perform(get("/api/epics/sync/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.productId").value(product.getId()));
    }

    @Test
    public void should_update_epic_is_hidden_true() throws Exception {
        Epic hidden = new Epic();
        BeanUtils.copyProperties(epic, hidden);
        hidden.setIsHidden(true);

        IsHiddenDTO hiddenDTO = new IsHiddenDTO(true);

        when(epicService.updateIsHidden(1L, hiddenDTO)).thenReturn(hidden);

        mockMvc.perform(put("/api/epics/1/hide")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapper.writeValueAsString(hiddenDTO))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.isHidden").value(true));
    }

}
