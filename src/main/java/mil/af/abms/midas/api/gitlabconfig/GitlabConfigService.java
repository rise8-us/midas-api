package mil.af.abms.midas.api.gitlabconfig;

import javax.transaction.Transactional;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.gitlabconfig.dto.CreateUpdateGitlabConfigDTO;
import mil.af.abms.midas.api.gitlabconfig.dto.GitlabConfigDTO;

@Service
public class GitlabConfigService extends AbstractCRUDService<GitlabConfig, GitlabConfigDTO, GitlabConfigRepository> {


    @Autowired
    public GitlabConfigService(GitlabConfigRepository repository) {
        super(repository, GitlabConfig.class, GitlabConfigDTO.class);
    }

    @Transactional
    public GitlabConfig create(CreateUpdateGitlabConfigDTO dto) {
        GitlabConfig configNew = new GitlabConfig();
        configNew.setBaseUrl(dto.getBaseUrl());
        configNew.setName(dto.getName());
        configNew.setDescription(dto.getDescription());
        configNew.setToken(dto.getToken());

        return repository.save(configNew);
   }

    @Transactional
    public GitlabConfig updateById(Long id, CreateUpdateGitlabConfigDTO dto) {
        GitlabConfig configToUpdate = findById(id);
        configToUpdate.setBaseUrl(dto.getBaseUrl());
        configToUpdate.setName(dto.getName());
        configToUpdate.setDescription(dto.getDescription());
        Optional.ofNullable(dto.getToken()).ifPresent(configToUpdate::setToken);

        return repository.save(configToUpdate);
   }

}
