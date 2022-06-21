package mil.af.abms.midas.api.product;

import static mil.af.abms.midas.api.helper.SprintDateHelper.getAllSprintDates;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.dtos.AppGroupDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.dtos.SprintProductMetricsDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.issue.Issue;
import mil.af.abms.midas.api.issue.IssueService;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.personnel.dto.UpdatePersonnelDTO;
import mil.af.abms.midas.api.product.dto.CreateProductDTO;
import mil.af.abms.midas.api.product.dto.ProductDTO;
import mil.af.abms.midas.api.product.dto.ProductInterfaceDTO;
import mil.af.abms.midas.api.product.dto.UpdateProductDTO;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ProductService extends AbstractCRUDService<Product, ProductDTO, ProductRepository> {

    private final SourceControlService sourceControlService;

    private ProjectService projectService;
    private TagService tagService;
    private PersonnelService personnelService;
    private IssueService issueService;

    public ProductService(ProductRepository repository, SourceControlService sourceControlService) {
        super(repository, Product.class, ProductDTO.class);
        this.sourceControlService = sourceControlService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) { this.projectService = projectService; }
    @Autowired
    public void setTagService(TagService tagService) { this.tagService = tagService; }
    @Autowired
    public void setPersonnelService(PersonnelService personnelService) { this.personnelService = personnelService; }
    @Autowired
    public void setIssueService(IssueService issueService) { this.issueService = issueService; }

    @Transactional
    public Product create(CreateProductDTO dto) {
        CreatePersonnelDTO createPersonnelDTO = Optional.ofNullable(dto.getPersonnel()).isPresent() ?
                dto.getPersonnel() : new CreatePersonnelDTO();
        Personnel personnel = personnelService.create(createPersonnelDTO);

        Product newProduct = Builder.build(Product.class)
                .with(p -> p.setName(dto.getName()))
                .with(p -> p.setDescription(dto.getDescription()))
                .with(p -> p.setVision(dto.getVision()))
                .with(p -> p.setMission(dto.getMission()))
                .with(p -> p.setProblemStatement(dto.getProblemStatement()))
                .with(p -> p.setGitlabGroupId(dto.getGitlabGroupId()))
                .with(p -> p.setPersonnel(personnel))
                .with(p -> p.setSourceControl(sourceControlService.findByIdOrNull(dto.getSourceControlId())))
                .with(p -> p.setRoadmapType(dto.getRoadmapType()))
                .get();
        updateRequiredNotNullFields(dto, newProduct);

        Product newProductSaved = repository.save(newProduct);
        projectService.addProductToProjects(newProductSaved, newProductSaved.getProjects());

        return newProductSaved;
    }

    @Transactional
    public Product updateById(Long id, UpdateProductDTO dto) {

        var originalProduct = findById(id);
        var product = new Product();
        BeanUtils.copyProperties(originalProduct, product);
        var originalProjects = product.getProjects();
        UpdatePersonnelDTO updatePersonnelDTO = dto.getPersonnel();

        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setVision(dto.getVision());
        product.setMission(dto.getMission());
        product.setProblemStatement(dto.getProblemStatement());
        product.setGitlabGroupId(dto.getGitlabGroupId());
        product.setSourceControl(sourceControlService.findByIdOrNull(dto.getSourceControlId()));
        product.setRoadmapType(dto.getRoadmapType());

        updateRequiredNotNullFields(dto, product);

        Optional.ofNullable(updatePersonnelDTO).ifPresent(personnelDTO -> {
            Personnel personnel = personnelService.updateById(product.getPersonnel().getId(), personnelDTO);
            product.setPersonnel(personnel);
        });

        projectService.updateProjectsWithProduct(originalProjects, product.getProjects(), product);

        return repository.save(product);
    }

    protected void updateRequiredNotNullFields(ProductInterfaceDTO dto, Product product) {
        Optional.ofNullable(dto.getTagIds()).ifPresent(tagIds ->
                product.setTags(tagIds.stream().map(tagService::findById).collect(Collectors.toSet())));
        Optional.ofNullable(dto.getProjectIds()).ifPresent(projectIds ->
                product.setProjects(projectIds.stream().map(projectService::findById).collect(Collectors.toSet())));
    }
    
    @Transactional
    public Product updateIsArchivedById(Long id, IsArchivedDTO isArchivedDTO) {
        var product = findById(id);
        product.setIsArchived(isArchivedDTO.getIsArchived());

        product.getProjects().forEach(p -> projectService.archive(p.getId(), isArchivedDTO));
        return repository.save(product);
    }

    @Transactional
    public Product findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Product.class.getSimpleName(), "name", name));
    }

    public List<Product> getAll() {
        return repository.findAll();
    }

    public boolean validateUniqueSourceControlAndGitlabGroup(AppGroupDTO appGroupDTO) {
        Integer gitlabGroupIdToCheck = appGroupDTO.getGitlabGroupId();
        Long sourceControlIdToCheck = appGroupDTO.getSourceControlId();
        String nameToCheck = appGroupDTO.getName();
        List<Product> allProducts = getAll().stream()
                .filter(p ->
                        !p.getName().equals(nameToCheck) && p.getGitlabGroupId() != null && p.getSourceControl() != null
                ).collect(Collectors.toList());

        for (Product product : allProducts) {
            Integer groupId = product.getGitlabGroupId();
            SourceControl sourceControl = product.getSourceControl();
            boolean isDuplicate = gitlabGroupIdToCheck.equals(groupId) && sourceControlIdToCheck.equals(sourceControl.getId());
            if (isDuplicate) return false;
        }
        return true;
    }

    public TreeMap<LocalDate, List<SprintProductMetricsDTO>> getSprintMetrics(Long id, LocalDate startDate, Integer duration, Integer sprints) {
        TreeMap<LocalDate, List<SprintProductMetricsDTO>> metricsMap = new TreeMap<>();

        Product foundProduct = findById(id);

        List<LocalDate> allDates = getAllSprintDates(startDate, duration, sprints);

        allDates.forEach(date -> {
            List<SprintProductMetricsDTO> dtos = new ArrayList<>();
            dtos.add(populateProductMetrics(dtos, date, foundProduct, duration));
            metricsMap.put(date, dtos);
        });

        return metricsMap;
    }

    public SprintProductMetricsDTO populateProductMetrics(List<SprintProductMetricsDTO> dtos, LocalDate currentDate, Product product, int duration) {
        List<Issue> allIssues = issueService.getAllIssuesByProductId(product.getId()).stream().filter(issue ->
                Optional.ofNullable(issue.getCompletedAt()).isPresent() &&
                        issue.getCompletedAt().toLocalDate().isBefore(currentDate.plusDays(duration)) && (issue.getCompletedAt().toLocalDate().isAfter(currentDate) || issue.getCompletedAt().toLocalDate().isEqual(currentDate))
        ).collect(Collectors.toList());

        long totalWeight = 0;
        for (Issue issue : allIssues) {
            totalWeight += issue.getWeight();
        }

        return new SprintProductMetricsDTO(product.getName(), totalWeight, allIssues.size());

    }

}
