package mil.af.abms.midas.api.application;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.application.dto.ApplicationDTO;
import mil.af.abms.midas.api.application.dto.CreateApplicationDTO;
import mil.af.abms.midas.api.application.dto.UpdateApplicationDTO;
import mil.af.abms.midas.api.application.dto.UpdateApplicationIsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ApplicationService extends AbstractCRUDService<Application, ApplicationDTO, ApplicationRepository> {

    private UserService userService;
    private ProjectService projectService;
    private TagService tagService;

    public ApplicationService(ApplicationRepository repository) {
        super(repository, Application.class, ApplicationDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }
    @Autowired
    public void setProjectService(ProjectService projectService) { this.projectService = projectService; }
    @Autowired
    public void setTagService(TagService tagService) { this.tagService = tagService; }
    
    @Transactional
    public Application create(CreateApplicationDTO createApplicationDTO) {
        Application newApplication = Builder.build(Application.class)
                .with(a -> a.setName(createApplicationDTO.getName()))
                .with(a -> a.setDescription(createApplicationDTO.getDescription()))
                .with(a -> a.setProductManager(getUser(createApplicationDTO.getProductManagerId())))
                .with(a -> a.setTags(createApplicationDTO.getTagIds().stream().map(tagService::getObject)
                        .collect(Collectors.toSet())))
                .get();

        Set<Project> projects = createApplicationDTO.getProjectIds().stream().map(projectService::getObject).collect(Collectors.toSet());
        newApplication.setProjects(projects);
        newApplication = repository.save(newApplication);

        for (Project project : projects) {
           projectService.addApplicationToProject(newApplication, project);
        }

        return newApplication;
    }

    @Transactional
    public Application findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Application.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Application updateById(Long id, UpdateApplicationDTO updateApplicationDTO) {
        User user = userService.getObject(updateApplicationDTO.getProductManagerId());

        Application application = getObject(id);
        application.setName(updateApplicationDTO.getName());
        application.setProductManager(user);
        application.setDescription(updateApplicationDTO.getDescription());
        application.setTags(updateApplicationDTO.getTagIds().stream()
                .map(tagService::getObject).collect(Collectors.toSet()));
        application.setProjects(updateApplicationDTO.getProjectIds().stream()
                .map(projectService::getObject).collect(Collectors.toSet()));

        return repository.save(application);
    }
    
    @Transactional
    public Application updateIsArchivedById(Long id, UpdateApplicationIsArchivedDTO updateApplicationIsArchivedDTO) {
        Application application = getObject(id);
        application.setIsArchived(updateApplicationIsArchivedDTO.getIsArchived());
        
        return repository.save(application);
    }

    private User getUser(Long userId) {
        return userId != null ? userService.getObject(userId) : null;
    }

}
