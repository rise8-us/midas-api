package mil.af.abms.midas.api.product;

import static mil.af.abms.midas.api.helper.SprintDateHelper.getAllSprintDates;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import mil.af.abms.midas.api.release.Release;
import mil.af.abms.midas.api.release.ReleaseService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class ProductService extends AbstractCRUDService<Product, ProductDTO, ProductRepository> {

    private IssueService issueService;
    private PersonnelService personnelService;
    private ProjectService projectService;
    private ReleaseService releaseService;
    private SourceControlService sourceControlService;
    private TagService tagService;

    @Autowired
    public void setIssueService(IssueService issueService) { this.issueService = issueService; }
    @Autowired
    public void setPersonnelService(PersonnelService personnelService) { this.personnelService = personnelService; }
    @Autowired
    public void setProjectService(ProjectService projectService) { this.projectService = projectService; }
    @Autowired
    public void setReleaseService(ReleaseService releaseService) { this.releaseService = releaseService; }
    @Autowired
    public void setSourceControlService(SourceControlService sourceControlService) { this.sourceControlService = sourceControlService; }
    @Autowired
    public void setTagService(TagService tagService) { this.tagService = tagService; }

    public ProductService(ProductRepository repository) {
        super(repository, Product.class, ProductDTO.class);
    }

    @Transactional
    public Product create(CreateProductDTO dto) {
        CreatePersonnelDTO createPersonnelDTO = Optional.ofNullable(dto.getPersonnel()).isPresent() ?
                dto.getPersonnel() : new CreatePersonnelDTO();
        Personnel personnel = personnelService.create(createPersonnelDTO);

        Product newProduct = Builder.build(Product.class)
                .with(p -> p.setName(dto.getName()))
                .with(p -> p.setAcronym(dto.getAcronym()))
                .with(p -> p.setCoreDomain(dto.getCoreDomain()))
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
        product.setAcronym(dto.getAcronym());
        product.setCoreDomain(dto.getCoreDomain());
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

    public boolean validateUniqueSourceControlAndGitlabGroup(AppGroupDTO appGroupDTO) {
        Integer gitlabGroupIdToCheck = appGroupDTO.getGitlabGroupId();
        Long sourceControlIdToCheck = appGroupDTO.getSourceControlId();
        String nameToCheck = appGroupDTO.getName();
        List<Product> allProducts = getAll().stream().filter(p ->
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

    public List<SprintProductMetricsDTO> getSprintMetrics(Long id, LocalDate startDate, Integer duration, Integer sprints) {
        Product foundProduct = findById(id);
        List<LocalDate> allDates = getAllSprintDates(startDate, duration, sprints);

        return allDates.stream().map(date -> populateProductMetrics(date, foundProduct, duration)).collect(Collectors.toList());
    }

    public SprintProductMetricsDTO populateProductMetrics(LocalDate currentDate, Product product, int potentialDuration) {
        LocalDate theoreticEndDate = currentDate.plusDays(potentialDuration);
        LocalDate endDate = theoreticEndDate.isAfter(LocalDate.now()) ? LocalDate.now() : theoreticEndDate;
        long duration = ChronoUnit.DAYS.between(currentDate, endDate);
        long totalWeight = 0;

        List<Issue> allIssues = issueService.getAllIssuesByProductId(product.getId()).stream().filter(issue ->
                Optional.ofNullable(issue.getCompletedAt()).isPresent() &&
                        issue.getCompletedAt().toLocalDate().isBefore(endDate) && (issue.getCompletedAt().toLocalDate().isAfter(currentDate) || issue.getCompletedAt().toLocalDate().isEqual(currentDate))
        ).collect(Collectors.toList());

        for (Issue issue : allIssues) { totalWeight += issue.getWeight(); }
        long finalTotalWeight = totalWeight;

        Set<Release> releases = product.getReleases().stream().filter(release ->
                release.getReleasedAt().isAfter(currentDate.atStartOfDay()) &&
                release.getReleasedAt().isBefore(endDate.atTime(LocalTime.MAX))
        ).collect(Collectors.toSet());

        float averageIssueDuration = calculateLeadTimeForChange(releases);

        return Builder.build(SprintProductMetricsDTO.class)
                .with(d -> d.setDate(currentDate))
                .with(d -> d.setDeliveredPoints(finalTotalWeight))
                .with(d -> d.setDeliveredStories(allIssues.size()))
                .with(d -> d.setReleaseFrequency((float) releases.size() / duration))
                .with(d -> d.setLeadTimeForChangeInMinutes(averageIssueDuration))
                .get();
    }

    protected float calculateLeadTimeForChange(Set<Release> releases) {
        long totalIssueDuration = 0;
        long issuesCount = 0;

        for (Release release : releases) {
            Long projectId = release.getProject().getId();

            Optional<Release> previousRelease = releaseService.getPreviousReleaseByProjectIdAndReleasedAt(projectId, release.getReleasedAt());
            LocalDateTime previousReleasedAt = previousRelease.isPresent() ? previousRelease.get().getReleasedAt() : LocalDateTime.of(2000, 1, 1, 1, 0, 0);

            List<Issue> issuesInRelease = issueService.findAllIssuesByProjectIdAndCompletedAtDateRange(projectId, previousReleasedAt, release.getReleasedAt());

            totalIssueDuration += issuesInRelease.stream()
                    .map(issue -> ChronoUnit.MINUTES.between(issue.getCompletedAt(), release.getReleasedAt()))
                    .mapToLong(Long::longValue).sum();
            issuesCount += issuesInRelease.size();
        }

        return issuesCount != 0 ? (float) totalIssueDuration / issuesCount : -1F;
    }

}
