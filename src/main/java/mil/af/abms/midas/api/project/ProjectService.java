package mil.af.abms.midas.api.project;

import javax.transaction.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.coverage.CoverageService;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
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

    private final TeamService teamService;
    private final TagService tagService;
    private final SourceControlService sourceControlService;

    private ProductService productService;
    private CoverageService coverageService;

    @Autowired
    public ProjectService(SourceControlService sourceControlService, ProjectRepository repository, TeamService teamService, TagService tagService) {
        super(repository, Project.class, ProjectDTO.class);
        this.sourceControlService = sourceControlService;
        this.teamService = teamService;
        this.tagService = tagService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setCoverageService(CoverageService coverageService) {
        this.coverageService = coverageService;
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
                .get();

        return repository.save(newProject);
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

        return repository.save(foundProject);
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
    public Project archive(Long id, ArchiveProjectDTO archiveProjectDTO) {
        var projectToArchive = findById(id);
        projectToArchive.setTeam(null);
        projectToArchive.setIsArchived(archiveProjectDTO.getIsArchived());
        return repository.save(projectToArchive);
    }

    @Scheduled(fixedRate = 3600000)
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

    protected void removeProduct(Project project) {
        project.setProduct(null);
        repository.save(project);
    }

    public void updateProjectsWithProduct(Set<Project> existingProjects, Set<Project> updatedProjects, Product product) {
        existingProjects.stream().filter(e -> !updatedProjects.contains(e)).forEach(this::removeProduct);
        updatedProjects.forEach(u -> addProductToProject(product, u));
    }

}
