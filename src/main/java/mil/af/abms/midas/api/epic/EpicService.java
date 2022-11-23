package mil.af.abms.midas.api.epic;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
import mil.af.abms.midas.api.dtos.PaginationProgressDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;
import mil.af.abms.midas.enums.SyncStatus;

@Slf4j
@Service
public class EpicService extends AbstractCRUDService<Epic, EpicDTO, EpicRepository> {

    private CompletionService completionService;
    private PortfolioService portfolioService;
    private ProductService productService;
    private final SimpMessageSendingOperations websocket;

    @Autowired
    public void setCompletionService(CompletionService completionService) {
        this.completionService = completionService;
    }

    @Autowired
    public void setPortfolioService(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public EpicService(EpicRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Epic.class, EpicDTO.class);
        this.websocket = websocket;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void runScheduledEpicSync() {
        for (Product product : productService.getAll()) {
            if (product.getIsArchived() == Boolean.FALSE) {
                gitlabEpicSync(product);
            }
        }
        for (Portfolio portfolio : portfolioService.getAll()) {
            if (portfolio.getIsArchived() == Boolean.FALSE) {
                gitlabEpicSync(portfolio);
            }
        }
    }

    private Epic createOrUpdate(Integer iid, AppGroup appGroup) {
        Long sourceControlId = appGroup.getSourceControl().getId();
        Integer groupId = appGroup.getGitlabGroupId();
        GitLabEpic epicConversion = getEpicFromClient(appGroup, iid);
        String uId = generateUniqueId(sourceControlId, groupId, iid);

        Epic epicToSave = repository.findByEpicUid(uId)
                .map(epic -> syncEpic(epicConversion, epic))
                .orElseGet(() -> convertToEpic(epicConversion, appGroup));
        return repository.save(epicToSave);
    }

    public Epic createOrUpdateForProduct(AddGitLabEpicWithProductDTO dto) {
        AppGroup product = productService.findById(dto.getProductId());
        return createOrUpdate(dto.getIId(), product);
    }

    public Epic createOrUpdateForPortfolio(AddGitLabEpicWithPortfolioDTO dto) {
        AppGroup product = portfolioService.findById(dto.getPortfolioId());
        return createOrUpdate(dto.getIId(), product);
    }

    @Transactional
    public Epic updateByIdForProduct(Long id) {
        Epic foundEpic = findById(id);
        Product product = foundEpic.getProduct();
        return updateOrDeleteEpic(foundEpic, getEpicFromClient(product, foundEpic.getEpicIid()));
    }

    @Transactional
    public Epic updateByIdForPortfolio(Long id) {
        Epic foundEpic = findById(id);
        Portfolio portfolio = foundEpic.getPortfolio();
        return updateOrDeleteEpic(foundEpic, getEpicFromClient(portfolio, foundEpic.getEpicIid()));
    }

    private Epic updateOrDeleteEpic(Epic foundEpic, GitLabEpic epicFromClient) {
        try {
            Epic epicToSave = syncEpic(epicFromClient, foundEpic);
            return repository.save(epicToSave);
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

    public void removeAllUntrackedEpicsForProducts(Long productId, List<Epic> fetchedEpicsSet) {
        Set<Integer> epicIids = fetchedEpicsSet.stream().map(Epic::getEpicIid).collect(Collectors.toSet());
        List<Epic> midasProductEpics = getAllEpicsByProductId(productId);
        Set<Integer> midasProductEpicIids = getEpicIids(midasProductEpics);

        midasProductEpicIids.removeAll(epicIids);
        midasProductEpics.removeIf(epic -> !midasProductEpicIids.contains(epic.getEpicIid()));
        midasProductEpics.forEach(this::updateCompletionType);

        repository.deleteAll(midasProductEpics);
    }

    public void removeAllUntrackedEpicsForPortfolios(Long portfolioId, List<Epic> fetchedEpicsSet) {
        Set<Integer> epicIids = fetchedEpicsSet.stream().map(Epic::getEpicIid).collect(Collectors.toSet());
        List<Epic> midasPortfolioEpics = getAllEpicsByPortfolioId(portfolioId);
        Set<Integer> midasPortfolioEpicIids = getEpicIids(midasPortfolioEpics);

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

    public List<Epic> gitlabEpicSync(AppGroup appGroup) {
        if (!hasGitlabDetails(appGroup)) {
            return List.of();
        }

        PaginationProgressDTO paginationProgressDTO = new PaginationProgressDTO();
        paginationProgressDTO.setId(appGroup.getId());
        GitLab4JClient client = getGitlabClient(appGroup);
        int totalPageCount = client.getTotalEpicsPages(appGroup);
        if (totalPageCount == -1) {
            paginationProgressDTO.setStatus(SyncStatus.SYNC_ERROR);
            websocket.convertAndSend("/topic/fetchGitlabEpicsPagination", paginationProgressDTO);
            return List.of();
        }

        List<Epic> allEpics = new ArrayList<>();

        for (int i = 1; i <= totalPageCount; i++) {
            allEpics.addAll(processEpics(client.fetchGitLabEpicByPage(appGroup, i), appGroup));
            paginationProgressDTO.setValue((double) i / totalPageCount);
            if (i == totalPageCount) {
                paginationProgressDTO.setStatus(SyncStatus.SYNCED);
            }
            websocket.convertAndSend("/topic/fetchGitlabEpicsPagination", paginationProgressDTO);
        }

        if (appGroup instanceof Product) {
            removeAllUntrackedEpicsForProducts(appGroup.getId(), allEpics);
        }
        if (appGroup instanceof Portfolio) {
            removeAllUntrackedEpicsForPortfolios(appGroup.getId(), allEpics);
        }

        return repository.saveAll(allEpics);
    }

    public List<Epic> processEpics(List<GitLabEpic> epics, AppGroup appGroup) {
        Long sourceControlId = appGroup.getSourceControl().getId();
        Integer groupId = appGroup.getGitlabGroupId();

        return epics.stream()
                .map(e ->
                        repository.findByEpicUid(generateUniqueId(sourceControlId, groupId, e.getEpicIid()))
                                .map(epic -> syncEpic(e, epic))
                                .orElseGet(() -> convertToEpic(e, appGroup))
                ).collect(Collectors.toList());
    }

    public boolean canAddEpic(Integer iid, AppGroup appGroup) {
        GitLab4JClient client = getGitlabClient(appGroup);
        return client.epicExistsByIdAndGroupId(iid, appGroup.getGitlabGroupId());
    }

    protected String generateUniqueId(Long sourceControlId, Integer gitlabGroupId, Integer epicIId) {
        return String.format("%d-%d-%d", sourceControlId, gitlabGroupId, epicIId);
    }

    protected GitLabEpic getEpicFromClient(AppGroup appGroup, Integer epicIid) {
        GitLab4JClient client = getGitlabClient(appGroup);
        return client.getEpicFromGroup(appGroup.getGitlabGroupId(), epicIid);
    }

    protected GitLab4JClient getGitlabClient(AppGroup appGroup) {
        return new GitLab4JClient(appGroup.getSourceControl());
    }

    protected Epic convertToEpic(GitLabEpic gitLabEpic, AppGroup appGroup) {
        String uId = generateUniqueId(appGroup.getSourceControl().getId(), appGroup.getGitlabGroupId(), gitLabEpic.getEpicIid());
        Epic newEpic = new Epic();
        BeanUtils.copyProperties(gitLabEpic, newEpic);
        newEpic.setSyncedAt(LocalDateTime.now());
        newEpic.setEpicUid(uId);
        if (appGroup instanceof Product) {
            newEpic.setProduct((Product) appGroup);
        }
        if (appGroup instanceof Portfolio) {
            newEpic.setPortfolio((Portfolio) appGroup);
        }

        return newEpic;
    }

    protected Epic syncEpic(GitLabEpic gitLabEpic, Epic epic) {
        BeanUtils.copyProperties(gitLabEpic, epic);

        epic.setSyncedAt(LocalDateTime.now());

        return epic;
    }

    protected boolean hasGitlabDetails(AppGroup appGroup) {
        return !appGroup.getIsArchived() &&
                appGroup.getGitlabGroupId() != null &&
                appGroup.getSourceControl() != null &&
                appGroup.getSourceControl().getToken() != null &&
                appGroup.getSourceControl().getBaseUrl() != null;
    }
}
