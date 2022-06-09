package mil.af.abms.midas.api.sourcecontrol;

import javax.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.sourcecontrol.dto.CreateUpdateSourceControlDTO;
import mil.af.abms.midas.api.sourcecontrol.dto.SourceControlDTO;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabProject;

@Service
public class SourceControlService extends AbstractCRUDService<SourceControl, SourceControlDTO, SourceControlRepository> {


    @Autowired
    public SourceControlService(SourceControlRepository repository) {
        super(repository, SourceControl.class, SourceControlDTO.class);
    }

    @Transactional
    public SourceControl create(CreateUpdateSourceControlDTO dto) {
        SourceControl configNew = new SourceControl();
        configNew.setBaseUrl(dto.getBaseUrl());
        configNew.setName(dto.getName());
        configNew.setDescription(dto.getDescription());
        configNew.setToken(dto.getToken());

        return repository.save(configNew);
   }

    @Transactional
    public SourceControl updateById(Long id, CreateUpdateSourceControlDTO dto) {
        SourceControl configToUpdate = findById(id);
        configToUpdate.setBaseUrl(dto.getBaseUrl());
        configToUpdate.setName(dto.getName());
        configToUpdate.setDescription(dto.getDescription());
        Optional.ofNullable(dto.getToken()).ifPresent(t -> {
            if (!t.isEmpty()) {
                configToUpdate.setToken(t);
            }
        });

        return repository.save(configToUpdate);
   }

    public List<GitLabProject> getAllGitlabProjectsForGroup(Long sourceControlId, Integer gitlabGroupId) {
        var sourceControl = findById(sourceControlId);
        var client = getGitlabClient(sourceControl);
        return client.getProjectsFromGroup(gitlabGroupId);
    }

    protected GitLab4JClient getGitlabClient(SourceControl sourceControl) {
        return new GitLab4JClient(sourceControl);
    }

}
