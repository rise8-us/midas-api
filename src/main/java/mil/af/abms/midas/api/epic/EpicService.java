package mil.af.abms.midas.api.epic;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.AppGroup;
import mil.af.abms.midas.api.completion.CompletionService;
import mil.af.abms.midas.api.dtos.AddGitLabEpicWithPortfolioDTO;
import mil.af.abms.midas.api.dtos.AddGitLabEpicWithProductDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;

@Slf4j
@Service
public class EpicService extends AbstractCRUDService<Epic, EpicDTO, EpicRepository> {

    private ProductService productService;
    private PortfolioService portfolioService;
    private CompletionService completionService;
    private final SimpMessageSendingOperations websocket;

    private static final String TOTAL = "total";
    private static final String COMPLETED = "completed";

    public EpicService(EpicRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Epic.class, EpicDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Autowired
    public void setCompletionService(CompletionService completionService) {
        this.completionService = completionService;
    }

    public Epic createForProduct(AddGitLabEpicWithProductDTO dto) {
        var product = productService.findById(dto.getProductId());
        var sourceControlId = product.getSourceControl().getId();
        var groupId = product.getGitlabGroupId();
        var epicConversion = getEpicFromClient(product, dto.getIId());
        var uId = generateUniqueId(sourceControlId, groupId, dto.getIId());

        return repository.findByEpicUid(uId)
                .map(epic -> syncEpic(epicConversion, epic))
                .orElseGet(() -> convertToEpic(epicConversion, product));

    }

    public Epic createForPortfolio(AddGitLabEpicWithPortfolioDTO dto) {
        var portfolio = portfolioService.findById(dto.getPortfolioId());
        var sourceControlId = portfolio.getSourceControl().getId();
        var groupId = portfolio.getGitlabGroupId();
        var epicConversion = getEpicFromClient(portfolio, dto.getIId());
        var uId = generateUniqueId(sourceControlId, groupId, dto.getIId());

        return repository.findByEpicUid(uId)
                .map(epic -> syncEpic(epicConversion, epic))
                .orElseGet(() -> convertToEpic(epicConversion, portfolio));
    }

    @Transactional
    public Epic updateByIdForProduct(Long id) {
        Epic foundEpic = findById(id);
        Product product = foundEpic.getProduct();
        return syncOrDeleteEpic(foundEpic, getEpicFromClient(product, foundEpic.getEpicIid()));
    }

    @Transactional
    public Epic updateByIdForPortfolio(Long id) {
        Epic foundEpic = findById(id);
        Portfolio portfolio = foundEpic.getPortfolio();
        return syncOrDeleteEpic(foundEpic, getEpicFromClient(portfolio, foundEpic.getEpicIid()));
    }

    private Epic syncOrDeleteEpic(Epic foundEpic, GitLabEpic epicFromClient) {
        try {
            return syncEpic(epicFromClient, foundEpic);
        } catch (Exception e) {
            foundEpic.getCompletions().forEach(c -> completionService.setCompletionTypeToFailure(c.getId()));

            repository.delete(foundEpic);
            return null;
        }
    }

    public Epic updateIsHidden(Long id, IsHiddenDTO dto) {
        Epic epic = findById(id);

        epic.setIsHidden(dto.getIsHidden());

        return repository.save(epic);
    }

    public List<Epic> getAllEpicsByProductId(Long productId) {
        return repository.findAllEpicsByProductId(productId).orElse(List.of());
    }

    public List<Epic> getAllEpicsByPortfolioId(Long portfolioId) {
        return repository.findAllEpicsByPortfolioId(portfolioId).orElse(List.of());
    }

    public void removeAllUntrackedEpicsForProducts(Long productId, Set<Epic> gitlabEpicsList) {
        var epicIids = gitlabEpicsList.stream().map(Epic::getEpicIid).collect(Collectors.toSet());
        var midasProductEpics = getAllEpicsByProductId(productId);
        var midasProductEpicIids = getEpicIids(midasProductEpics);

        midasProductEpicIids.removeAll(epicIids);
        midasProductEpics.removeIf(epic -> !midasProductEpicIids.contains(epic.getEpicIid()));
        midasProductEpics.forEach(this::updateCompletionType);

        repository.deleteAll(midasProductEpics);
    }

    public void removeAllUntrackedEpicsForPortfolios(Long portfolioId, Set<Epic> gitlabEpicsList) {
        var epicIids = gitlabEpicsList.stream().map(Epic::getEpicIid).collect(Collectors.toSet());
        var midasPortfolioEpics = getAllEpicsByPortfolioId(portfolioId);
        var midasPortfolioEpicIids = getEpicIids(midasPortfolioEpics);

        midasPortfolioEpicIids.removeAll(epicIids);
        midasPortfolioEpics.removeIf(epic -> !midasPortfolioEpicIids.contains(epic.getEpicIid()));
        midasPortfolioEpics.forEach(this::updateCompletionType);
        repository.deleteAll(midasPortfolioEpics);
    }

    private Set<Integer> getEpicIids(List<Epic> epics) {
        return epics.stream().map(Epic::getEpicIid).collect(Collectors.toSet());
    }

    private void updateCompletionType(Epic epic) {
        Optional.ofNullable(epic.getCompletions()).ifPresent(completions -> completions.forEach(c ->
                completionService.setCompletionTypeToFailure(c.getId())
        ));
    }

    public Product getProductById(Long productId) {
        return productService.findById(productId);
    }

    public Portfolio getPortfolioById(Long portfolioId) {
        return portfolioService.findById(portfolioId);
    }

    public Set<Epic> gitlabEpicSync(AppGroup appGroup) {
        GitLab4JClient client = getGitlabClient(appGroup);
        int totalPageCount = client.getTotalEpicsPages(appGroup);

        Set<Epic> allEpics = new HashSet<>();

        for (int i = 1; i <= totalPageCount; i++) {
            allEpics.addAll(processEpics(client.fetchGitLabEpicByPage(appGroup, i), appGroup));
        }

        if (appGroup instanceof Product) {
            removeAllUntrackedEpicsForProducts(appGroup.getId(), allEpics);
        }
        if (appGroup instanceof Portfolio) {
            removeAllUntrackedEpicsForPortfolios(appGroup.getId(), allEpics);
        }
        return allEpics;
    }

    public Set<Epic> processEpics(List<GitLabEpic> epics, AppGroup appGroup) {
        var sourceControlId = appGroup.getSourceControl().getId();
        var groupId = appGroup.getGitlabGroupId();

        return epics.stream()
                .map(e ->
                    repository.findByEpicUid(generateUniqueId(sourceControlId, groupId, e.getEpicIid()))
                            .map(epic -> syncEpic(e, epic))
                            .orElseGet(() -> convertToEpic(e, appGroup))
                ).collect(Collectors.toSet());
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledEpicSync() {
        for (Long productId : productService.getAllProductIds()) {
            gitlabEpicSync(getProductById(productId));
        }
        for (Long portfolioId : portfolioService.getAllPortfolioIds()) {
            gitlabEpicSync(getPortfolioById(portfolioId));
        }
    }

    public boolean canAddEpic(Integer iid, AppGroup appGroup) {
        var client = getGitlabClient(appGroup);
        return client.epicExistsByIdAndGroupId(iid, appGroup.getGitlabGroupId());
    }

    protected String generateUniqueId(Long sourceControlId, Integer gitlabGroupId, Integer epicIId) {
        return String.format("%d-%d-%d", sourceControlId, gitlabGroupId, epicIId);
    }

    protected GitLabEpic getEpicFromClient(AppGroup appGroup, Integer epicIid) {
        var client = getGitlabClient(appGroup);
        return client.getEpicFromGroup(appGroup.getGitlabGroupId(), epicIid);
    }

    protected GitLab4JClient getGitlabClient(AppGroup appGroup) {
        return new GitLab4JClient(appGroup.getSourceControl());
    }

    protected Epic convertToEpic(GitLabEpic gitLabEpic, AppGroup appGroup) {
        var uId = generateUniqueId(appGroup.getSourceControl().getId(), appGroup.getGitlabGroupId(), gitLabEpic.getEpicIid());
        var newEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, newEpic);
        newEpic.setSyncedAt(LocalDateTime.now());
        newEpic.setEpicUid(uId);
        if (appGroup instanceof Product) {
            newEpic.setProduct((Product) appGroup);
        }
        if (appGroup instanceof Portfolio) {
            newEpic.setPortfolio((Portfolio) appGroup);
        }

        Epic updatedEpic = setWeights(newEpic);

        return repository.save(updatedEpic);
    }

    protected Epic syncEpic(GitLabEpic gitLabEpic, Epic epic) {
        BeanUtils.copyProperties(gitLabEpic, epic);

        Epic updatedEpic = setWeights(epic);
        completionService.updateLinkedEpic(epic);
        epic.setSyncedAt(LocalDateTime.now());

        return repository.save(updatedEpic);
    }

    protected boolean hasGitlabDetails(AppGroup appGroup) {
        return !appGroup.getIsArchived() &&
                appGroup.getGitlabGroupId() != null &&
                appGroup.getSourceControl() != null &&
                appGroup.getSourceControl().getToken() != null &&
                appGroup.getSourceControl().getBaseUrl() != null;
    }

    protected Epic setWeights(Epic epic) {
        AppGroup appGroup = epic.getProduct() == null ? epic.getPortfolio() : epic.getProduct();

        var client = getGitlabClient(appGroup);
        Optional<GitLabEpic> foundEpic = Optional.of(client.getEpicFromGroup(appGroup.getGitlabGroupId(), epic.getEpicIid()));

        var weights = getAllEpicWeights(client, foundEpic);
        epic.setTotalWeight(weights.get(TOTAL).longValue());
        epic.setCompletedWeight(weights.get(COMPLETED).longValue());
        return epic;
    }


    public Map<String, Integer> getAllEpicWeights(GitLab4JClient client, Optional<GitLabEpic> epic) {
        var base = new HashMap<>(Map.ofEntries(
                Map.entry(TOTAL, 0),
                Map.entry(COMPLETED, 0)
        ));
        epic.ifPresent(e -> {
            var subEpics = client.getSubEpicsFromEpicAndGroup(e.getGroupId(), e.getEpicIid());

            for (GitLabEpic subEpic : subEpics) {
                var subWeights = getAllEpicWeights(client, Optional.of(subEpic));

                base.put(TOTAL, subWeights.get(TOTAL) + base.get(TOTAL));
                base.put(COMPLETED, subWeights.get(COMPLETED) + base.get(COMPLETED));
            }

            var issues = client.getIssuesFromEpic(e.getGroupId(), e.getEpicIid());
            var totalWeight = 0;
            var completedWeight = 0;

            for (GitLabIssue issue : issues) {
                var weightToAdd = issue.getWeight() == null ? 1 : issue.getWeight();
                totalWeight += weightToAdd;
                if (Objects.equals(issue.getState(), "closed")) {
                    completedWeight += weightToAdd;
                }
            }

            base.put(TOTAL, totalWeight + base.get(TOTAL));
            base.put(COMPLETED, completedWeight + base.get(COMPLETED));
        });
        return base;
    }

}
