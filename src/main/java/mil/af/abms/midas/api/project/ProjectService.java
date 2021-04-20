package mil.af.abms.midas.api.project;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.project.dto.ArchiveProjectDTO;
import mil.af.abms.midas.api.project.dto.CreateProjectDTO;
import mil.af.abms.midas.api.project.dto.ProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectDTO;
import mil.af.abms.midas.api.project.dto.UpdateProjectJourneyMapDTO;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ProjectService extends AbstractCRUDService<Project, ProjectDTO, ProjectRepository> {

    private ProductService productService;
    private final TeamService teamService;
    private final TagService tagService;

    @Autowired
    public ProjectService(ProjectRepository repository, TeamService teamService, TagService tagService) {
        super(repository, Project.class, ProjectDTO.class);
        this.teamService = teamService;
        this.tagService = tagService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Transactional
    public Project create(CreateProjectDTO createProjectDTO) {
        Set<Tag> tags = createProjectDTO.getTagIds().stream().map(tagService::getObject).collect(Collectors.toSet());
        Project newProject = Builder.build(Project.class)
                .with(p -> p.setName(createProjectDTO.getName()))
                .with(p -> p.setDescription(createProjectDTO.getDescription()))
                .with(p -> p.setProduct(productService.findByIdOrNull(createProjectDTO.getProductId())))
                .with(p -> p.setTags(tags))
                .with(p -> p.setGitlabProjectId(createProjectDTO.getGitlabProjectId())).get();

        return repository.save(newProject);
    }

    @Transactional
    public Project findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Project.class.getSimpleName(), "name", name));
    }

    @Transactional
    public Project updateById(Long id, UpdateProjectDTO updateProjectDTO) {
        Project foundProject = getObject(id);
        Set<Tag> tags = updateProjectDTO.getTagIds().stream().map(tagService::getObject).collect(Collectors.toSet());

        foundProject.setTags(tags);
        foundProject.setTeam(teamService.findByIdOrNull(updateProjectDTO.getTeamId()));
        foundProject.setName(updateProjectDTO.getName());
        foundProject.setDescription(updateProjectDTO.getDescription());
        foundProject.setGitlabProjectId(updateProjectDTO.getGitlabProjectId());
        foundProject.setProduct(productService.findByIdOrNull(updateProjectDTO.getProductId()));

        return repository.save(foundProject);
    }

    @Transactional
    public void removeTagFromProject(Long tagId, Project project) {
        Set<Tag> tagsToKeep = project.getTags().stream().filter(t -> !t.getId().equals(tagId)).collect(Collectors.toSet());
        project.setTags(tagsToKeep);
        repository.save(project);
    }

    @Transactional
    public Project updateJourneyMapById(Long id, UpdateProjectJourneyMapDTO updateProjectJourneyMapDTO) {
        Project foundProject = getObject(id);
        foundProject.setProjectJourneyMap(updateProjectJourneyMapDTO.getProjectJourneyMap());

        return repository.save(foundProject);
    }

    @Transactional
    public Project archive(Long id, ArchiveProjectDTO archiveProjectDTO) {
        Project projectToArchive = getObject(id);
        projectToArchive.setTeam(null);
        projectToArchive.setIsArchived(archiveProjectDTO.getIsArchived());
        return repository.save(projectToArchive);
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

}
