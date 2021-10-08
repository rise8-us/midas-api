package mil.af.abms.midas.api.epic;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.dtos.AddGitLabEpicDTO;
import mil.af.abms.midas.api.epic.dto.EpicDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.sourcecontrol.SourceControl;
import mil.af.abms.midas.clients.gitlab.GitLab4JClient;
import mil.af.abms.midas.clients.gitlab.models.EpicConversion;

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

    public Set<Epic> getAllGitlabEpicsForProduct(Long productId) {
        var product = productService.findById(productId);
        var epicConversions = getGitlabClient(product)
                .getEpicsFromGroup(product.getSourceControl(), product.getGitlabGroupId());

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
        return Long.parseLong(String.format("%d%d%d", sourceControlId , groupId, epicIId));
    }

    protected EpicConversion getEpicFromClient(Product product, Integer epicIid) {
        return getGitlabClient(product).getEpicFromGroup(product.getSourceControl(), product.getGitlabGroupId(), epicIid);
    }

    protected GitLab4JClient getGitlabClient(Product product) {
        var url = Optional.ofNullable(product.getSourceControl()).map(SourceControl::getBaseUrl).orElse(null);
        var token = Optional.ofNullable(product.getSourceControl()).map(SourceControl::getToken).orElse(null);
        return new GitLab4JClient(url, token);
    }

    protected Epic convertToEpic(EpicConversion epicConversion, Product product) {
        var uId = generateUniqueId(product.getSourceControl().getId(), product.getGitlabGroupId(), epicConversion.getEpicIid());
        var newEpic = new Epic();
        BeanUtils.copyProperties(epicConversion, newEpic);
        newEpic.setSyncedAt(LocalDateTime.now());
        newEpic.setEpicUid(uId);
        newEpic.setProduct(product);
        return repository.save(newEpic);
    }

    protected Epic syncEpic(EpicConversion epicConversion, Epic epic) {
        BeanUtils.copyProperties(epicConversion, epic);
        epic.setSyncedAt(LocalDateTime.now());
        return repository.save(epic);
    }
}
