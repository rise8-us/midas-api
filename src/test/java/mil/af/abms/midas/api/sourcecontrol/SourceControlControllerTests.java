package mil.af.abms.midas.api.sourcecontrol;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.sourcecontrol.dto.CreateUpdateSourceControlDTO;
import mil.af.abms.midas.clients.gitlab.models.GitLabProject;

@WebMvcTest({SourceControlController.class})
public class SourceControlControllerTests extends ControllerTestHarness {
    
    @Autowired
    SourceControlController sourceControlController;
    @MockBean
    private SourceControlService sourceControlService;
    
    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(1L))
            .with(g -> g.setToken("foobarbaz"))
            .with(g -> g.setName("bar"))
            .with(g -> g.setDescription("foo"))
            .with(g -> g.setBaseUrl("http://foo.bar"))
            .get();
    private final CreateUpdateSourceControlDTO cDto = Builder.build(CreateUpdateSourceControlDTO.class)
            .with(d -> d.setToken("foobarbaz"))
            .with(d -> d.setName("bar"))
            .with(d -> d.setDescription("foo"))
            .with(d -> d.setBaseUrl("http://foo.bar"))
            .get();
    final private GitLabProject gitLabProject = Builder.build(GitLabProject.class)
            .with(p -> p.setGitlabProjectId(7))
            .with(p -> p.setName("Midas UI"))
            .get();

    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_source_control() throws Exception {
        when(sourceControlService.create(any())).thenReturn(sourceControl);

        mockMvc.perform(post("/api/sourceControls")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(cDto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(cDto.getName()));
    }

    @Test
    public void should_update_source_control() throws Exception {
        CreateUpdateSourceControlDTO uDto = new CreateUpdateSourceControlDTO();
        BeanUtils.copyProperties(cDto, uDto);
        uDto.setName("bar");

        when(sourceControlService.updateById(any(), any())).thenReturn(sourceControl);

        mockMvc.perform(put("/api/sourceControls/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(cDto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(uDto.getName()));
    }

    @Test
    void should_get_all_projects_from_gitlab_group() throws Exception {
        doReturn(List.of(gitLabProject)).when(sourceControlService).getAllGitlabProjectsForGroup(sourceControl.getId(), 123);
        mockMvc.perform(get("/api/sourceControls/1/group/123/projects")).andExpect(status().isOk());
    }

}
