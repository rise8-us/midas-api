package mil.af.abms.midas.api.gitlabconfig;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import mil.af.abms.midas.api.ControllerTestHarness;
import mil.af.abms.midas.api.gitlabconfig.dto.CreateUpdateGitlabConfigDTO;
import mil.af.abms.midas.api.helper.Builder;

@WebMvcTest({GitlabConfigController.class})
public class GitlabConfigControllerTests extends ControllerTestHarness {
    
    @Autowired
    GitlabConfigController gitlabConfigController;
    @MockBean
    private GitlabConfigService gitlabConfigService;
    
    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();
    
    private final GitlabConfig gitlabConfig = Builder.build(GitlabConfig.class)
            .with(g -> g.setId(1L))
            .with(g -> g.setToken("foobarbaz"))
            .with(g -> g.setName("bar"))
            .with(g -> g.setDescription("foo"))
            .with(g -> g.setBaseUrl("http://foo.bar"))
            .with(g -> g.setCreationDate(CREATION_DATE))
            .get();
    private final CreateUpdateGitlabConfigDTO cDto = Builder.build(CreateUpdateGitlabConfigDTO.class)
            .with(d -> d.setToken("foobarbaz"))
            .with(d -> d.setName("bar"))
            .with(d -> d.setDescription("foo"))
            .with(d -> d.setBaseUrl("http://foo.bar"))
            .get();


    @BeforeEach
    public void init() throws Exception {
        when(userService.findByKeycloakUid(any())).thenReturn(Optional.of(authUser));
    }

    @Test
    public void should_create_gitlab_config() throws Exception {
        when(gitlabConfigService.create(any())).thenReturn(gitlabConfig);

        mockMvc.perform(post("/api/gitlabConfigs")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(cDto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(cDto.getName()));
    }

    @Test
    public void should_update_gitlab_config() throws Exception {
        CreateUpdateGitlabConfigDTO uDto = new CreateUpdateGitlabConfigDTO();
        BeanUtils.copyProperties(cDto, uDto);
        uDto.setName("bar");

        when(gitlabConfigService.updateById(any(), any())).thenReturn(gitlabConfig);

        mockMvc.perform(put("/api/gitlabConfigs/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(cDto))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.name").value(uDto.getName()));
    }
}
