package mil.af.abms.midas.api.epic;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.dtos.AddGitLabEpicDTO;
import mil.af.abms.midas.api.dtos.IsHiddenDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.GitLabEpic;

@Service
public class EpicService extends AbstractCRUDService<Epic, EpicDTO, EpicRepository> {

    private ProductService productService;

    public EpicService(EpicRepository repository) {
        super(repository, Epic.class, EpicDTO.class);
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public Epic create(AddGitLabEpicDTO dto) {
        var product = productService.findById(dto.getProductId());
        var epicConversion = getEpicFromClient(product, dto.getIId());
        var uId = generateUniqueId(product, dto.getIId());

        return repository.findByEpicUid(uId)
                .map(epic -> syncEpic(epicConversion, epic))
                .orElseGet(() -> convertToEpic(epicConversion, product));

    }

    public Epic updateById(Long id) {
        var foundEpic = findById(id);
        var product = foundEpic.getProduct();
        var epicConversion = getEpicFromClient(product, foundEpic.getEpicIid());

        return syncEpic(epicConversion, foundEpic);
    }

    public Epic updateIsHidden(Long id, IsHiddenDTO dto) {
        Epic epic = findById(id);

        epic.setIsHidden(dto.getIsHidden());

        return repository.save(epic);
    }

    public Set<Epic> getAllGitlabEpicsForProduct(Long productId) {
        var product = productService.findById(productId);
        var epicConversions = getGitlabClient(product)
                .getEpicsFromGroup(product.getGitlabGroupId());

        return epicConversions.stream()
                .map(e ->
                     repository.findByEpicUid(generateUniqueId(product, e.getEpicIid()))
                             .map(epic -> syncEpic(e, epic))
                             .orElseGet(() ->  convertToEpic(e, product))
                ).collect(Collectors.toSet()
        );
    }

    public boolean canAddEpic(Integer iid, Product product) {
        var gitLab4JClient = getGitlabClient(product);
        return gitLab4JClient.epicExistsByIdAndGroupId(iid, product.getGitlabGroupId());
    }

    protected Long generateUniqueId(Long sourceControlId, Integer gitlabGroupId, Integer epicIId) {
        return Long.parseLong(String.format("%d%d%d", sourceControlId, gitlabGroupId, epicIId));
    }

    protected Long generateUniqueId(Product product, Integer epicIId) {
        var sourceControlId = product.getSourceControl().getId();
        var groupId = product.getGitlabGroupId();
        return Long.parseLong(String.format("%d%d%d", sourceControlId, groupId, epicIId));
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
        return repository.save(newEpic);
    }

    protected Epic syncEpic(GitLabEpic gitLabEpic, Epic epic) {
        BeanUtils.copyProperties(gitLabEpic, epic);
        epic.setSyncedAt(LocalDateTime.now());
        return repository.save(epic);
    }

}
