package mil.af.abms.midas.api.sourcecontrol;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.sourcecontrol.dto.CreateUpdateSourceControlDTO;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabProject;

@ExtendWith(SpringExtension.class)
@Import(SourceControlService.class)
class SourceControlServiceTests {

    @MockBean
    SimpMessageSendingOperations websocket;
    @SpyBean
    SourceControlService sourceControlService;
    @MockBean
    SourceControlRepository sourceControlRepository;
    @Mock
    GitLab4JClient gitLab4JClient;
    @Captor
    ArgumentCaptor<SourceControl> sourceControlCaptor;

    private static final LocalDateTime CREATION_DATE = LocalDateTime.now();

    private final SourceControl sourceControl = Builder.build(SourceControl.class)
            .with(g -> g.setId(1L))
            .with(g -> g.setToken("foobarbaz"))
            .with(g -> g.setName("bar"))
            .with(g -> g.setDescription("foo"))
            .with(g -> g.setBaseUrl("http://foo.bar"))
            .with(g -> g.setCreationDate(CREATION_DATE))
            .get();
    final private GitLabProject gitLabProject = Builder.build(GitLabProject.class)
            .with(p -> p.setGitlabProjectId(7))
            .with(p -> p.setName("Midas UI"))
            .get();
    
    @Test
    void should_update_create_source_control() {
        CreateUpdateSourceControlDTO cDto = Builder.build(CreateUpdateSourceControlDTO.class)
                .with(d -> d.setBaseUrl("http://foo.bar"))
                .with(d -> d.setDescription("foo"))
                .with(d -> d.setName("bar"))
                .with(d -> d.setToken("mockToken"))
                .get();

        when(sourceControlRepository.save(any())).thenReturn(sourceControl);

        sourceControlService.create(cDto);

        verify(sourceControlRepository).save(sourceControlCaptor.capture());
        SourceControl capturedConfig = sourceControlCaptor.getValue();

        assertThat(capturedConfig.getDescription()).isEqualTo(cDto.getDescription());
        assertThat(capturedConfig.getName()).isEqualTo(cDto.getName());
        assertThat(capturedConfig.getBaseUrl()).isEqualTo(cDto.getBaseUrl());
        assertThat(capturedConfig.getToken()).isEqualTo(cDto.getToken());

    }

    @Test
    void should_update_update_source_control() {
        CreateUpdateSourceControlDTO uDto = Builder.build(CreateUpdateSourceControlDTO.class)
                .with(d -> d.setBaseUrl("http://foo.bar.baz"))
                .with(d -> d.setDescription("fooU"))
                .with(d -> d.setName("barU"))
                .with(d -> d.setToken("mockTokenU"))
                .get();

        doReturn(sourceControl).when(sourceControlService).findById(1L);
        when(sourceControlRepository.save(any())).thenReturn(sourceControl);

        sourceControlService.updateById(1L, uDto);

        verify(sourceControlRepository).save(sourceControlCaptor.capture());
        SourceControl capturedConfig = sourceControlCaptor.getValue();

        assertThat(capturedConfig.getDescription()).isEqualTo(uDto.getDescription());
        assertThat(capturedConfig.getName()).isEqualTo(uDto.getName());
        assertThat(capturedConfig.getBaseUrl()).isEqualTo(uDto.getBaseUrl());
        assertThat(capturedConfig.getToken()).isEqualTo(uDto.getToken());

    }

    @Test
    void should_update_update_source_control_and_skip_token_update() {
        CreateUpdateSourceControlDTO uDto = Builder.build(CreateUpdateSourceControlDTO.class)
                .with(d -> d.setBaseUrl("http://foo.bar.baz"))
                .with(d -> d.setDescription("fooU"))
                .with(d -> d.setName("barU"))
                .get();

        doReturn(sourceControl).when(sourceControlService).findById(1L);
        when(sourceControlRepository.save(any())).thenReturn(sourceControl);

        sourceControlService.updateById(1L, uDto);

        verify(sourceControlRepository).save(sourceControlCaptor.capture());
        SourceControl capturedConfig = sourceControlCaptor.getValue();

        assertThat(capturedConfig.getDescription()).isEqualTo(uDto.getDescription());
        assertThat(capturedConfig.getName()).isEqualTo(uDto.getName());
        assertThat(capturedConfig.getBaseUrl()).isEqualTo(uDto.getBaseUrl());
        assertThat(capturedConfig.getToken()).isEqualTo(sourceControl.getToken());

    }

    @Test
    void should_update_update_source_control_but_not_token() {
        CreateUpdateSourceControlDTO uDto = Builder.build(CreateUpdateSourceControlDTO.class)
                .with(d -> d.setBaseUrl("http://foo.bar.baz"))
                .with(d -> d.setDescription("fooU"))
                .with(d -> d.setName("barU"))
                .with(d -> d.setToken(""))
                .get();

        doReturn(sourceControl).when(sourceControlService).findById(1L);
        when(sourceControlRepository.save(any())).thenReturn(sourceControl);

        sourceControlService.updateById(1L, uDto);

        verify(sourceControlRepository).save(sourceControlCaptor.capture());
        SourceControl capturedConfig = sourceControlCaptor.getValue();

        assertThat(capturedConfig.getDescription()).isEqualTo(uDto.getDescription());
        assertThat(capturedConfig.getName()).isEqualTo(uDto.getName());
        assertThat(capturedConfig.getBaseUrl()).isEqualTo(uDto.getBaseUrl());
        assertThat(capturedConfig.getToken()).isEqualTo(sourceControl.getToken());
    }

    @Test
    void should_get_all_projects_for_group() {
        doReturn(sourceControl).when(sourceControlService).findById(sourceControl.getId());
        doReturn(gitLab4JClient).when(sourceControlService).getGitlabClient(sourceControl);
        doReturn(List.of(gitLabProject)).when(gitLab4JClient).getProjectsFromGroup(123);

        assertThat(sourceControlService.getAllGitlabProjectsForGroup(sourceControl.getId(), 123))
                .isEqualTo(List.of(gitLabProject));
    }

}
