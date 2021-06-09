package mil.af.abms.midas.api.gitlabconfig;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.gitlabconfig.dto.CreateUpdateGitlabConfigDTO;
import mil.af.abms.midas.api.helper.Builder;

@ExtendWith(SpringExtension.class)
@Import(GitlabConfigService.class)
public class GitlabConfigServiceTests {

    @SpyBean
    GitlabConfigService gitlabConfigService;
    @MockBean
    GitlabConfigRepository gitlabConfigRepository;
    @Captor
    ArgumentCaptor<GitlabConfig> gitlabConfigCaptor;

    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final GitlabConfig gitlabConfig = Builder.build(GitlabConfig.class)
            .with(g -> g.setId(1L))
            .with(g -> g.setToken("foobarbaz"))
            .with(g -> g.setName("bar"))
            .with(g -> g.setDescription("foo"))
            .with(g -> g.setBaseUrl("http://foo.bar"))
            .with(g -> g.setCreationDate(CREATION_DATE))
            .get();
    
    @Test
    public void should_update_create_gitlab_config() {
        CreateUpdateGitlabConfigDTO cDto = Builder.build(CreateUpdateGitlabConfigDTO.class)
                .with(d -> d.setBaseUrl("http://foo.bar"))
                .with(d -> d.setDescription("foo"))
                .with(d -> d.setName("bar"))
                .with(d -> d.setToken("mockToken"))
                .get();

        when(gitlabConfigRepository.save(any())).thenReturn(gitlabConfig);

        gitlabConfigService.create(cDto);

        verify(gitlabConfigRepository).save(gitlabConfigCaptor.capture());
        GitlabConfig capturedConfig = gitlabConfigCaptor.getValue();

        assertThat(capturedConfig.getDescription()).isEqualTo(cDto.getDescription());
        assertThat(capturedConfig.getName()).isEqualTo(cDto.getName());
        assertThat(capturedConfig.getBaseUrl()).isEqualTo(cDto.getBaseUrl());
        assertThat(capturedConfig.getToken()).isEqualTo(cDto.getToken());

    }

    @Test
    public void should_update_update_gitlab_config() {
        CreateUpdateGitlabConfigDTO uDto = Builder.build(CreateUpdateGitlabConfigDTO.class)
                .with(d -> d.setBaseUrl("http://foo.bar.baz"))
                .with(d -> d.setDescription("fooU"))
                .with(d -> d.setName("barU"))
                .with(d -> d.setToken("mockTokenU"))
                .get();

        doReturn(gitlabConfig).when(gitlabConfigService).getObject(1L);
        when(gitlabConfigRepository.save(any())).thenReturn(gitlabConfig);

        gitlabConfigService.updateById(1L, uDto);

        verify(gitlabConfigRepository).save(gitlabConfigCaptor.capture());
        GitlabConfig capturedConfig = gitlabConfigCaptor.getValue();

        assertThat(capturedConfig.getDescription()).isEqualTo(uDto.getDescription());
        assertThat(capturedConfig.getName()).isEqualTo(uDto.getName());
        assertThat(capturedConfig.getBaseUrl()).isEqualTo(uDto.getBaseUrl());
        assertThat(capturedConfig.getToken()).isEqualTo(uDto.getToken());

    }

    @Test
    public void should_update_update_gitlab_config_but_not_token() {
        CreateUpdateGitlabConfigDTO uDto = Builder.build(CreateUpdateGitlabConfigDTO.class)
                .with(d -> d.setBaseUrl("http://foo.bar.baz"))
                .with(d -> d.setDescription("fooU"))
                .with(d -> d.setName("barU"))
                .with(d -> d.setToken(null))
                .get();

        doReturn(gitlabConfig).when(gitlabConfigService).getObject(1L);
        when(gitlabConfigRepository.save(any())).thenReturn(gitlabConfig);

        gitlabConfigService.updateById(1L, uDto);

        verify(gitlabConfigRepository).save(gitlabConfigCaptor.capture());
        GitlabConfig capturedConfig = gitlabConfigCaptor.getValue();

        assertThat(capturedConfig.getDescription()).isEqualTo(uDto.getDescription());
        assertThat(capturedConfig.getName()).isEqualTo(uDto.getName());
        assertThat(capturedConfig.getBaseUrl()).isEqualTo(uDto.getBaseUrl());
        assertThat(capturedConfig.getToken()).isEqualTo(gitlabConfig.getToken());

    }

}
