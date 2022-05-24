package mil.af.abms.midas.api.epic;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import mil.af.abms.midas.api.AbstractCRUDService;
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

    private static final String TOTAL = "total";
    private static final String COMPLETED = "completed";

    public EpicService(EpicRepository repository) {
        super(repository, Epic.class, EpicDTO.class);
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
        var epicConversion = getProductEpicFromClient(product, dto.getIId());
        var uId = generateUniqueId(sourceControlId, groupId, dto.getIId());

        return repository.findByEpicUid(uId)
                .map(epic -> syncEpicForProduct(epicConversion, epic))
                .orElseGet(() -> convertToEpicForProduct(epicConversion, product));

    }

    public Epic createForPortfolio(AddGitLabEpicWithPortfolioDTO dto) {
        var portfolio = portfolioService.findById(dto.getPortfolioId());
        var sourceControlId = portfolio.getSourceControl().getId();
        var groupId = portfolio.getGitlabGroupId();
        var epicConversion = getPortfolioEpicFromClient(portfolio, dto.getIId());
        var uId = generateUniqueId(sourceControlId, groupId, dto.getIId());

        return repository.findByEpicUid(uId)
                .map(epic -> syncEpicForPortfolio(epicConversion, epic))
                .orElseGet(() -> convertToEpicForPortfolio(epicConversion, portfolio));

    }

    @Transactional
    public Epic updateByIdForProduct(Long id) {
        Epic foundEpic = findById(id);
        Product product = foundEpic.getProduct();
        try {
            GitLabEpic gitLabEpic = getProductEpicFromClient(product, foundEpic.getEpicIid());
            return syncEpicForProduct(gitLabEpic, foundEpic);
        } catch (Exception e) {
            foundEpic.getCompletions().forEach(c -> completionService.setCompletionTypeToFailure(c.getId()));

            repository.delete(foundEpic);
            return null;
        }
    }

    @Transactional
    public Epic updateByIdForPortfolio(Long id) {
        Epic foundEpic = findById(id);
        Portfolio portfolio = foundEpic.getPortfolio();
        try {
            GitLabEpic gitLabEpic = getPortfolioEpicFromClient(portfolio, foundEpic.getEpicIid());
            return syncEpicForPortfolio(gitLabEpic, foundEpic);
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

    public void removeAllUntrackedEpicsForProducts(Long productId, List<GitLabEpic> gitlabEpicsList) {
        var epicIids = gitlabEpicsList.stream().map(GitLabEpic::getEpicIid).collect(Collectors.toSet());
        var midasProductEpics = getAllEpicsByProductId(productId);
        var midasProductEpicIids = getEpicIids(midasProductEpics);
        midasProductEpicIids.removeAll(epicIids);
        midasProductEpics.removeIf(epic -> !midasProductEpicIids.contains(epic.getEpicIid()));
        midasProductEpics.forEach((this::updateCompletionType));
        repository.deleteAll(midasProductEpics);
    }

    public void removeAllUntrackedEpicsForPortfolios(Long portfolioId, List<GitLabEpic> gitlabEpicsList) {
        var epicIids = gitlabEpicsList.stream().map(GitLabEpic::getEpicIid).collect(Collectors.toSet());
        var midasPortfolioEpics = getAllEpicsByPortfolioId(portfolioId);
        var midasPortfolioEpicIids = getEpicIids(midasPortfolioEpics);
        midasPortfolioEpicIids.removeAll(epicIids);
        midasPortfolioEpics.removeIf(epic -> !midasPortfolioEpicIids.contains(epic.getEpicIid()));
        midasPortfolioEpics.forEach((this::updateCompletionType));
        repository.deleteAll(midasPortfolioEpics);
    }

    private Set<Integer> getEpicIids(List<Epic> epics) {
        return epics.stream().map(Epic::getEpicIid).collect(Collectors.toSet());
    }

    private void updateCompletionType(Epic epic) {
        epic.getCompletions().forEach(c -> {
            completionService.setCompletionTypeToFailure(c.getId());
        });
    }

    public Set<Epic> getAllGitlabEpicsForProduct(Long productId) {
        var product = productService.findById(productId);
        if (hasGitlabDetailsForProduct(product)) {
            var allEpicsInGitLab = getGitlabClientForProduct(product)
                    .getEpicsFromGroup(product.getGitlabGroupId());
            var sourceControlId = product.getSourceControl().getId();
            var groupId = product.getGitlabGroupId();
            removeAllUntrackedEpicsForProducts(productId, allEpicsInGitLab);

            return allEpicsInGitLab.stream()
                    .map(e ->
                            repository.findByEpicUid(generateUniqueId(sourceControlId, groupId, e.getEpicIid()))
                                    .map(epic -> syncEpicForProduct(e, epic))
                                    .orElseGet(() -> convertToEpicForProduct(e, product))
                    ).collect(Collectors.toSet());
        }

        return Set.of();
    }

    public Set<Epic> getAllGitlabEpicsForPortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioService.findById(portfolioId);
        if (hasGitlabDetailsForPortfolio(portfolio)) {
            var allEpicsInGitLab = getGitlabClientForPortfolio(portfolio)
                    .getEpicsFromGroup(portfolio.getGitlabGroupId());
            var sourceControlId = portfolio.getSourceControl().getId();
            var groupId = portfolio.getGitlabGroupId();
            removeAllUntrackedEpicsForPortfolios(portfolioId, allEpicsInGitLab);
            return allEpicsInGitLab.stream()
                    .map(e ->
                            repository.findByEpicUid(generateUniqueId(sourceControlId, groupId, e.getEpicIid()))
                                    .map(epic -> syncEpicForPortfolio(e, epic))
                                    .orElseGet(() -> convertToEpicForPortfolio(e, portfolio))
                    ).collect(Collectors.toSet());
        }

        return Set.of();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledEpicSync() {
        for (Long productId : productService.getAllProductIds()) {
                getAllGitlabEpicsForProduct(productId);
        }
        for (Long portfolioId : portfolioService.getAllPortfolioIds()) {
                getAllGitlabEpicsForPortfolio(portfolioId);
        }
    }

    public boolean canAddEpicWithProduct(Integer iid, Product product) {
        var client = getGitlabClientForProduct(product);
        return client.epicExistsByIdAndGroupId(iid, product.getGitlabGroupId());
    }

    public boolean canAddEpicWithPortfolio(Integer iid, Portfolio portfolio) {
        var client = getGitlabClientForPortfolio(portfolio);
        return client.epicExistsByIdAndGroupId(iid, portfolio.getGitlabGroupId());
    }

    protected String generateUniqueId(Long sourceControlId, Integer gitlabGroupId, Integer epicIId) {
        return String.format("%d-%d-%d", sourceControlId, gitlabGroupId, epicIId);
    }

    protected GitLabEpic getProductEpicFromClient(Product product, Integer epicIid) {
        var client = getGitlabClientForProduct(product);
        return client.getEpicFromGroup(product.getGitlabGroupId(), epicIid);
    }

    protected GitLabEpic getPortfolioEpicFromClient(Portfolio portfolio, Integer epicIid) {
        var client = getGitlabClientForPortfolio(portfolio);
        return client.getEpicFromGroup(portfolio.getGitlabGroupId(), epicIid);
    }

    protected GitLab4JClient getGitlabClientForProduct(Product product) {
        return new GitLab4JClient(product.getSourceControl());
    }

    protected GitLab4JClient getGitlabClientForPortfolio(Portfolio portfolio) {
        return new GitLab4JClient(portfolio.getSourceControl());
    }

    protected Epic convertToEpicForProduct(GitLabEpic gitLabEpic, Product product) {
        var uId = generateUniqueId(product.getSourceControl().getId(), product.getGitlabGroupId(), gitLabEpic.getEpicIid());
        var newEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, newEpic);
        newEpic.setSyncedAt(LocalDateTime.now());
        newEpic.setEpicUid(uId);
        newEpic.setProduct(product);

        Epic updatedEpic = setWeightsForProduct(newEpic);

        return repository.save(updatedEpic);
    }

    protected Epic convertToEpicForPortfolio(GitLabEpic gitLabEpic, Portfolio portfolio) {
        var uId = generateUniqueId(portfolio.getSourceControl().getId(), portfolio.getGitlabGroupId(), gitLabEpic.getEpicIid());
        var newEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, newEpic);
        newEpic.setSyncedAt(LocalDateTime.now());
        newEpic.setEpicUid(uId);
        newEpic.setPortfolio(portfolio);

        Epic updatedEpic = setWeightsForPortfolio(newEpic);

        return repository.save(updatedEpic);
    }

    protected Epic syncEpicForProduct(GitLabEpic gitLabEpic, Epic epic) {
        BeanUtils.copyProperties(gitLabEpic, epic);

        Epic updatedEpic = setWeightsForProduct(epic);
        completionService.updateLinkedEpic(epic);
        epic.setSyncedAt(LocalDateTime.now());

        return repository.save(updatedEpic);
    }

    protected Epic syncEpicForPortfolio(GitLabEpic gitLabEpic, Epic epic) {
        BeanUtils.copyProperties(gitLabEpic, epic);

        Epic updatedEpic = setWeightsForPortfolio(epic);
        completionService.updateLinkedEpic(epic);
        epic.setSyncedAt(LocalDateTime.now());

        return repository.save(updatedEpic);
    }

    protected boolean hasGitlabDetailsForProduct(Product product) {
        return !product.getIsArchived() &&
                product.getGitlabGroupId() != null &&
                product.getSourceControl() != null &&
                product.getSourceControl().getToken() != null &&
                product.getSourceControl().getBaseUrl() != null;
    }


    protected boolean hasGitlabDetailsForPortfolio(Portfolio portfolio) {
        return !portfolio.getIsArchived() &&
                portfolio.getGitlabGroupId() != null &&
                portfolio.getSourceControl() != null &&
                portfolio.getSourceControl().getToken() != null &&
                portfolio.getSourceControl().getBaseUrl() != null;
    }

    protected Epic setWeightsForProduct(Epic epic) {
        var client = getGitlabClientForProduct(epic.getProduct());
        Optional<GitLabEpic> foundEpic = Optional.of(client.getEpicFromGroup(epic.getProduct().getGitlabGroupId(), epic.getEpicIid()));

        var weights = getAllEpicWeights(client, foundEpic);
        epic.setTotalWeight(weights.get(TOTAL).longValue());
        epic.setCompletedWeight(weights.get(COMPLETED).longValue());
        return epic;
    }

    protected Epic setWeightsForPortfolio(Epic epic) {
        var client = getGitlabClientForPortfolio(epic.getPortfolio());
        Optional<GitLabEpic> foundEpic = Optional.of(client.getEpicFromGroup(epic.getPortfolio().getGitlabGroupId(), epic.getEpicIid()));

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
