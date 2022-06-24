package mil.af.abms.midas.api.project;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.coverage.CoverageService;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.dto.CreateProjectDTO;
import mil.af.abms.midas.api.project.dto.ProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectJourneyMapDTO;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabProject;
import mil.af.abms.midas.enums.SyncStatus;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Slf4j
@Service
public class ProjectService extends AbstractCRUDService<Project, ProjectDTO, ProjectRepository> {

    private CoverageService coverageService;
    private ProductService productService;
    private SourceControlService sourceControlService;
    private TagService tagService;
    private TeamService teamService;
    private UserService userService;
    private final SimpMessageSendingOperations websocket;

    @Autowired
    public void setCoverageService(CoverageService coverageService) {
        this.coverageService = coverageService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setSourceControlService(SourceControlService sourceControlService) {
        this.sourceControlService = sourceControlService;
    }

    @Autowired
    public void setTagService(TagService tagService) {
        this.tagService = tagService;
    }

    @Autowired
    public void setTeamService(TeamService teamService) {
        this.teamService = teamService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public ProjectService(ProjectRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Project.class, ProjectDTO.class);
        this.websocket = websocket;
    }

    @Transactional
    public Project create(CreateProjectDTO dto) {
        Set<Tag> tags = dto.getTagIds().stream().map(tagService::findById).collect(Collectors.toSet());
        Project newProject = Builder.build(Project.class)
                .with(p -> p.setName(dto.getName()))
                .with(p -> p.setDescription(dto.getDescription()))
                .with(p -> p.setProduct(productService.findByIdOrNull(dto.getProductId())))
                .with(p -> p.setTags(tags))
                .with(p -> p.setTeam(teamService.findByIdOrNull(dto.getTeamId())))
                .with(p -> p.setGitlabProjectId(dto.getGitlabProjectId()))
                .with(p -> p.setSourceControl(sourceControlService.findByIdOrNull(dto.getSourceControlId())))
                .with(p -> p.setOwner(userService.getUserBySecContext()))
                .get();

        var createdProject = repository.save(newProject);
        addCoverageOnCreateOrUpdate(createdProject);

        return createdProject;
    }

    @Transactional
    public Project createFromGitlab(GitLabProject gitlabProject) {
        return repository.findByGitlabProjectId(gitlabProject.getGitlabProjectId())
                .map(project -> syncProject(gitlabProject, project))
                .orElseGet(() -> convertToProject(gitlabProject));
    }

    @Transactional
    public Project findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Project.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Project updateById(Long id, UpdateProjectDTO dto) {
        var foundProject = findById(id);
        var tags = dto.getTagIds().stream().map(tagService::findById).collect(Collectors.toSet());

        foundProject.setTags(tags);
        foundProject.setTeam(teamService.findByIdOrNull(dto.getTeamId()));
        foundProject.setName(dto.getName());
        foundProject.setDescription(dto.getDescription());
        foundProject.setGitlabProjectId(dto.getGitlabProjectId());
        foundProject.setProduct(productService.findByIdOrNull(dto.getProductId()));
        foundProject.setSourceControl(sourceControlService.findByIdOrNull(dto.getSourceControlId()));

        var updatedProject = repository.save(foundProject);
        addCoverageOnCreateOrUpdate(updatedProject);

        return updatedProject;
    }

    public Project syncProjectWithGitlab(Long id) {
        var foundProject = findById(id);
        var gitlabProjectId = foundProject.getGitlabProjectId();
        var sourceControl = foundProject.getSourceControl();
        var gitlabProject = getGitlabClient(sourceControl).getGitLabProject(gitlabProjectId);

        return syncProject(gitlabProject, foundProject);
    }

    public void removeTagFromProject(Long tagId, Project project) {
        var tagsToKeep = project.getTags().stream().filter(t -> !t.getId().equals(tagId)).collect(Collectors.toSet());
        project.setTags(tagsToKeep);
        repository.save(project);
    }

    @Transactional
    public Project updateJourneyMapById(Long id, UpdateProjectJourneyMapDTO updateProjectJourneyMapDTO) {
        var foundProject = findById(id);
        foundProject.setProjectJourneyMap(updateProjectJourneyMapDTO.getProjectJourneyMap());

        return repository.save(foundProject);
    }

    @Transactional
    public Project archive(Long id, IsArchivedDTO isArchivedDTO) {
        var projectToArchive = findById(id);
        projectToArchive.setTeam(null);
        projectToArchive.setIsArchived(isArchivedDTO.getIsArchived());
        return repository.save(projectToArchive);
    }

    @Scheduled(fixedRate = 900000)
    public void scheduledCoverageUpdates() {
        var projects = repository.findAll(ProjectSpecifications.hasGitlabProjectId()).stream()
                .filter(p -> p.getSourceControl() != null).collect(Collectors.toList());
        projects.forEach(coverageService::updateCoverageForProject);
    }

    public void removeTagFromProjects(Long tagId, Set<Project> projects) {
        projects.forEach(p -> removeTagFromProject(tagId, p));
    }

    public void addProductToProjects(Product product, Set<Project> projects) {
       projects.forEach(project -> addProductToProject(product, project));
    }

    public void addProductToProject(Product product, Project project) {
        project.setProduct(product);
        repository.save(project);
    }

    public void updateProjectsWithProduct(Set<Project> existingProjects, Set<Project> updatedProjects, Product product) {
        existingProjects.stream().filter(e -> !updatedProjects.contains(e)).forEach(this::removeProduct);
        updatedProjects.forEach(u -> addProductToProject(product, u));
    }

    protected void removeProduct(Project project) {
        project.setProduct(null);
        repository.save(project);
    }

    protected void addCoverageOnCreateOrUpdate(Project project) {
        if (project.getSourceControl() != null && project.getGitlabProjectId() != null) {
            var coverage = coverageService.updateCoverageForProject(project);
            project.getCoverages().add(coverage);
            websocket.convertAndSend("/topic/update_project", project.toDto());
        }
    }

    protected Project convertToProject(GitLabProject gitLabProject) {
        var newProject = new Project();
        BeanUtils.copyProperties(gitLabProject, newProject);
        newProject.setCreationDate(gitLabProject.getGitlabCreationDate());
        //TODO: Save Avatar to S3
        return repository.save(newProject);
    }

    protected Project syncProject(GitLabProject gitLabProject, Project project) {
        BeanUtils.copyProperties(gitLabProject, project);
        project.setCreationDate(gitLabProject.getGitlabCreationDate());
        return repository.save(project);
    }

    protected GitLab4JClient getGitlabClient(SourceControl sourceControl) {
        return new GitLab4JClient(sourceControl);
    }

    public void updateReleaseSyncStatus(Long id, SyncStatus status) {
        Project project = findById(id);
        project.setReleaseSyncStatus(status);
        repository.save(project);
    }

    public void updateIssueSyncStatus(Long id, SyncStatus status) {
        Project project = findById(id);
        project.setIssueSyncStatus(status);
        repository.save(project);
    }

}
