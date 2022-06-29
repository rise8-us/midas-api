package mil.af.abms.midas.api.portfolio;

import javax.transaction.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.capability.CapabilityService;
import mil.af.abms.midas.api.dtos.AppGroupDTO;
import mil.af.abms.midas.api.dtos.IsArchivedDTO;
import mil.af.abms.midas.api.dtos.SprintProductMetricsDTO;
import mil.af.abms.midas.api.dtos.SprintSummaryPortfolioDTO;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.issue.Issue;
import mil.af.abms.midas.api.issue.IssueService;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.personnel.dto.CreatePersonnelDTO;
import mil.af.abms.midas.api.portfolio.dto.CreatePortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioDTO;
import mil.af.abms.midas.api.portfolio.dto.PortfolioInterfaceDTO;
import mil.af.abms.midas.api.portfolio.dto.UpdatePortfolioDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.release.Release;
import mil.af.abms.midas.api.release.ReleaseService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.api.sourcecontrol.SourceControlService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.exception.EntityNotFoundException;

@Service
public class PortfolioService extends AbstractCRUDService<Portfolio, PortfolioDTO, PortfolioRepository> {

    private CapabilityService capabilityService;
    private PersonnelService personnelService;
    private ProductService productService;
    private SourceControlService sourceControlService;
    private UserService userService;
    private ReleaseService releaseService;
    private IssueService issueService;

    @Autowired
    public void setCapabilityService(CapabilityService capabilityService) { this.capabilityService = capabilityService; }
    @Autowired
    public void setPersonnelService(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }
    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }
    @Autowired
    public void setSourceControlService(SourceControlService sourceControlService) { this.sourceControlService = sourceControlService; }
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @Autowired
    public void setReleaseService(ReleaseService releaseService) { this.releaseService = releaseService; }
    @Autowired
    public void setIssueService(IssueService issueService) { this.issueService = issueService; }

    public PortfolioService(PortfolioRepository repository) {
        super(repository, Portfolio.class, PortfolioDTO.class);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void setSprintStartDate() {
        for (Portfolio portfolio : getAll()) {
            if (portfolio.getIsArchived() == Boolean.FALSE) {
                compareSprintStartDateWithCurrentDate(portfolio);
            }
        }
    }

    @Transactional
    public Portfolio create(CreatePortfolioDTO dto) {
        CreatePersonnelDTO createPersonnelDTO = Optional.ofNullable(dto.getPersonnel()).isPresent() ?
                dto.getPersonnel() : new CreatePersonnelDTO();
        Personnel personnel = personnelService.create(createPersonnelDTO);

        Portfolio newPortfolio = Builder.build(Portfolio.class)
                .with(p -> p.setName(dto.getName()))
                .with(p -> p.setPersonnel(personnel))
                .with(p -> p.setSprintStartDate(Optional.ofNullable(dto.getSprintStartDate()).orElse(LocalDate.now())))
                .with(p -> p.setSprintDurationInDays(Optional.ofNullable(dto.getSprintDurationInDays()).orElse(7)))
                .get();

        updateCommonFields(dto, newPortfolio);

        return repository.save(newPortfolio);
    }

    @Transactional
    public Portfolio updateById(Long id, UpdatePortfolioDTO dto) {
        Portfolio foundPortfolio = findById(id);

        foundPortfolio.setName(dto.getName());
        Optional.ofNullable(dto.getPersonnel()).ifPresent(updatePersonnelDTO -> {
            Personnel personnel = personnelService.updateById(foundPortfolio.getPersonnel().getId(), dto.getPersonnel());
            foundPortfolio.setPersonnel(personnel);
        });

        Optional.ofNullable(dto.getGanttNote()).ifPresent(ganttNote -> {
            if (!Objects.equals(ganttNote, foundPortfolio.getGanttNote())) {
                foundPortfolio.setGanttNote(ganttNote);
                foundPortfolio.setGanttNoteModifiedAt(LocalDateTime.now());
                foundPortfolio.setGanttNoteModifiedBy(userService.getUserBySecContext());
            }
        });

        updateCommonFields(dto, foundPortfolio);

        return repository.save(foundPortfolio);
    }

    protected void updateCommonFields(PortfolioInterfaceDTO dto, Portfolio portfolio) {
        portfolio.setDescription(dto.getDescription());
        portfolio.setGitlabGroupId(dto.getGitlabGroupId());
        portfolio.setSourceControl(sourceControlService.findByIdOrNull(dto.getSourceControlId()));
        portfolio.setVision(dto.getVision());
        portfolio.setMission(dto.getMission());
        portfolio.setProblemStatement(dto.getProblemStatement());
        Optional.ofNullable(dto.getSprintStartDate()).ifPresent(portfolio::setSprintStartDate);
        Optional.ofNullable(dto.getSprintDurationInDays()).ifPresent(portfolio::setSprintDurationInDays);

        Optional.ofNullable(dto.getProductIds()).ifPresent(productIds ->
                portfolio.setProducts(productIds.stream().map(productService::findById).collect(Collectors.toSet())));
        Optional.ofNullable(dto.getCapabilityIds()).ifPresent(capabilityIds ->
                portfolio.setCapabilities(capabilityIds.stream().map(capabilityService::findById).collect(Collectors.toSet())));
    }

    @Transactional
    public Portfolio updateIsArchivedById(Long id, IsArchivedDTO isArchivedDTO) {
        Portfolio portfolio = findById(id);

        portfolio.setIsArchived(isArchivedDTO.getIsArchived());

        return repository.save(portfolio);
    }

    public Portfolio findByName(String name) {
        return repository.findByName(name).orElseThrow(
                () -> new EntityNotFoundException(Product.class.getSimpleName(), "name", name));
    }

    public boolean validateUniqueSourceControlAndGitlabGroup(AppGroupDTO appGroupDTO) {
        Integer gitlabGroupIdToCheck = appGroupDTO.getGitlabGroupId();
        Long sourceControlIdToCheck = appGroupDTO.getSourceControlId();
        String nameToCheck = appGroupDTO.getName();
        List<Portfolio> allPortfolios = getAll().stream()
                .filter(p ->
                        !p.getName().equals(nameToCheck) && p.getGitlabGroupId() != null && p.getSourceControl() != null
                ).collect(Collectors.toList());

        for (Portfolio portfolio : allPortfolios) {
            Integer groupId = portfolio.getGitlabGroupId();
            SourceControl sourceControl = portfolio.getSourceControl();
            boolean isDuplicate = gitlabGroupIdToCheck.equals(groupId) && sourceControlIdToCheck.equals(sourceControl.getId());
            if (isDuplicate) return false;
        }
        return true;
    }

    public Map<Long, List<SprintProductMetricsDTO>> getSprintMetrics(Long id, LocalDate startDate, Integer duration, Integer sprints) {
        Portfolio foundPortfolio = findById(id);
        List<Product> allProducts = new ArrayList<>(foundPortfolio.getProducts());
        HashMap<Long, List<SprintProductMetricsDTO>> metricsMap = new HashMap<>();

        allProducts.forEach(product -> {
            List<SprintProductMetricsDTO> dtos = new ArrayList<>(productService.getSprintMetrics(product.getId(), startDate, duration, sprints));
            metricsMap.put(product.getId(), dtos);
        });

        return metricsMap;
    }

    protected void compareSprintStartDateWithCurrentDate(Portfolio portfolio) {
        if (LocalDate.now().isAfter(portfolio.getSprintStartDate().plusDays(portfolio.getSprintDurationInDays() - 1L))) {
            portfolio.setSprintStartDate(portfolio.getSprintStartDate().plusDays(portfolio.getSprintDurationInDays() - 1L));
            repository.save(portfolio);
        }
    }

    protected List<Issue> getAllIssuesDeployedToProdForSprint(Portfolio portfolio, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        List<Issue> issuesToProd = new ArrayList<>();
        List<Project> allProjects = portfolio.getProducts().stream().flatMap(p -> p.getProjects().stream()).collect(Collectors.toList());

        allProjects.forEach(project -> {
            List<Release> releasesThisSprint = releaseService.filterReleasedAtByDateRange(new ArrayList<>(project.getReleases()), startDateTime, endDateTime);
            Release latestReleaseThisSprint = releasesThisSprint.stream().max(Comparator.comparing(Release::getReleasedAt)).orElse(null);

            if (latestReleaseThisSprint == null) { return; }
            LocalDateTime latestReleaseDateTime = latestReleaseThisSprint.getReleasedAt();

            List<Release> previousReleases = project.getReleases().stream()
                    .filter(r -> r.getReleasedAt().isBefore(startDateTime)).collect(Collectors.toList());
            Release latestPreviousRelease = previousReleases.stream().max(Comparator.comparing(Release::getReleasedAt)).orElse(null);

            if (latestPreviousRelease == null) {
                issuesToProd.addAll(issueService.filterCompletedAtByDateRange(
                        issueService.getAllIssuesByProjectId(project.getId()),
                        LocalDateTime.parse("1970-01-01T00:00"),
                        latestReleaseDateTime
                ));
                return;
            }

            issuesToProd.addAll(issueService.filterCompletedAtByDateRange(
                    issueService.getAllIssuesByProjectId(project.getId()),
                    latestPreviousRelease.getReleasedAt(),
                    latestReleaseDateTime
            ));
        });

        return issuesToProd;
    }

    public SprintSummaryPortfolioDTO getSprintMetricsSummary(Long id, String startDateStr, Integer duration) {
        Portfolio foundPortfolio = findById(id);
        if (startDateStr.equals("")) { startDateStr = LocalDate.now().toString(); }

        LocalDateTime startDateTime = LocalDate.parse(startDateStr).atStartOfDay();
        LocalDateTime endDateTime = LocalDate.parse(startDateStr).plusDays(Math.max(0, duration - 1)).atTime(LocalTime.MAX);

        List<Release> prodDeployments = releaseService.filterReleasedAtByDateRange(releaseService.getAllReleasesByPortfolioId(id), startDateTime, endDateTime);
        List<Issue> issuesToStaging = issueService.filterCompletedAtByDateRange(issueService.getAllIssuesByPortfolioId(id), startDateTime, endDateTime);
        List<Issue> issuesToProd = getAllIssuesDeployedToProdForSprint(foundPortfolio, startDateTime, endDateTime);

        return new SprintSummaryPortfolioDTO(prodDeployments.size(), issuesToStaging.size(), issuesToProd.size());
    }

    public List<Release> getPortfolioReleases(Long id) {
        Portfolio portfolio = findById(id);

        return portfolio.getProducts().stream()
                .flatMap(product -> product.getReleases().stream())
                .collect(Collectors.toList());
    }
}
