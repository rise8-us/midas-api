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
import mil.af.abms.midas.api.dtos.AddGitLabEpicDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;
import mil.af.abms.midas.clients.gitlab.models.GitLabIssue;

@Slf4j
@Service
public class EpicService extends AbstractCRUDService<Epic, EpicDTO, EpicRepository> {

    private ProductService productService;
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
    public void setCompletionService(CompletionService completionService) {
        this.completionService = completionService;
    }

    public Epic create(AddGitLabEpicDTO dto) {
        var product = productService.findById(dto.getProductId());
        var sourceControlId = product.getSourceControl().getId();
        var groupId = product.getGitlabGroupId();
        var epicConversion = getEpicFromClient(product, dto.getIId());
        var uId = generateUniqueId(sourceControlId, groupId, dto.getIId());

        return repository.findByEpicUid(uId)
                .map(epic -> syncEpic(epicConversion, epic))
                .orElseGet(() -> convertToEpic(epicConversion, product));

    }

    @Transactional
    public Epic updateById(Long id) {
        var foundEpic = findById(id);
        var product = foundEpic.getProduct();
        try {
            var gitLabEpic = getEpicFromClient(product, foundEpic.getEpicIid());
            return syncEpic(gitLabEpic, foundEpic);
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

    public void removeAllUntrackedEpics(Long productId, List<GitLabEpic> gitlabEpicsList) {
        var epicIids = gitlabEpicsList.stream().map(GitLabEpic::getEpicIid).collect(Collectors.toSet());
        var midasProductEpics = getAllEpicsByProductId(productId);
        var midasProductEpicIids = midasProductEpics.stream().map(Epic::getEpicIid).collect(Collectors.toSet());

        midasProductEpicIids.removeAll(epicIids);
        midasProductEpics.removeIf(epic -> !midasProductEpicIids.contains(epic.getEpicIid()));
        repository.deleteAll(midasProductEpics);
    }

    public Set<Epic> getAllGitlabEpicsForProduct(Long productId) {
        var product = productService.findById(productId);

        if (hasGitlabDetails(product)) {
            var allEpicsInGitLab = getGitlabClient(product)
                    .getEpicsFromGroup(product.getGitlabGroupId());
            var sourceControlId = product.getSourceControl().getId();
            var groupId = product.getGitlabGroupId();

            removeAllUntrackedEpics(productId, allEpicsInGitLab);

            return allEpicsInGitLab.stream()
                    .map(e ->
                            repository.findByEpicUid(generateUniqueId(sourceControlId, groupId, e.getEpicIid()))
                                    .map(epic -> syncEpic(e, epic))
                                    .orElseGet(() -> convertToEpic(e, product))
                    ).collect(Collectors.toSet());
        }

        return Set.of();
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledEpicSync() {
        for (Long productId : productService.getAllProductIds()) {
                getAllGitlabEpicsForProduct(productId);
        }
    }

    public boolean canAddEpic(Integer iid, Product product) {
        var client = getGitlabClient(product);
        return client.epicExistsByIdAndGroupId(iid, product.getGitlabGroupId());
    }

    protected String generateUniqueId(Long sourceControlId, Integer gitlabGroupId, Integer epicIId) {
        return String.format("%d-%d-%d", sourceControlId, gitlabGroupId, epicIId);
    }

    protected GitLabEpic getEpicFromClient(Product product, Integer epicIid) {
        var client = getGitlabClient(product);
        return client.getEpicFromGroup(product.getGitlabGroupId(), epicIid);
    }

    protected GitLab4JClient getGitlabClient(Product product) {
        return new GitLab4JClient(product.getSourceControl());
    }

    protected Epic convertToEpic(GitLabEpic gitLabEpic, Product product) {
        var uId = generateUniqueId(product.getSourceControl().getId(), product.getGitlabGroupId(), gitLabEpic.getEpicIid());
        var newEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, newEpic);
        newEpic.setSyncedAt(LocalDateTime.now());
        newEpic.setEpicUid(uId);
        newEpic.setProduct(product);

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

    protected boolean hasGitlabDetails(Product product) {
        return !product.getIsArchived() &&
                product.getGitlabGroupId() != null &&
                product.getSourceControl() != null &&
                product.getSourceControl().getToken() != null &&
                product.getSourceControl().getBaseUrl() != null;
    }

    protected Epic setWeights(Epic epic) {
        var client = getGitlabClient(epic.getProduct());
        Optional<GitLabEpic> foundEpic = Optional.of(client.getEpicFromGroup(epic.getProduct().getGitlabGroupId(), epic.getEpicIid()));

        var weights = getAllEpicWeights(client, foundEpic);
        epic.setTotalWeight(weights.get(TOTAL).longValue());
        epic.setCompletedWeight(weights.get(COMPLETED).longValue());
        return epic;
    }

    public HashMap<String, Integer> getAllEpicWeights(GitLab4JClient client, Optional<GitLabEpic> epic) {
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
