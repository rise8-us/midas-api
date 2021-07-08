package mil.af.abms.midas.api.assertion;

import javax.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.assertion.dto.BlockerAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.AssertionStatus;

@Service
public class AssertionService extends AbstractCRUDService<Assertion, AssertionDTO, AssertionRepository> {

    private UserService userService;
    private ProductService productService;
    private CommentService commentService;

    public AssertionService(AssertionRepository repository) {
        super(repository, Assertion.class, AssertionDTO.class);
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Autowired
    public void setCommentService(CommentService commentService) {
        this.commentService = commentService;
    }

    @Transactional
    public Assertion create(CreateAssertionDTO dto) {
        Assertion newAssertion = Builder.build(Assertion.class)
                .with(a -> a.setText(dto.getText()))
                .with(a -> a.setType(dto.getType()))
                .with(a -> a.setStatus(AssertionStatus.NOT_STARTED))
                .with(a -> a.setProduct(productService.getObject(dto.getProductId())))
                .with(a -> a.setParent(findByIdOrNull(dto.getParentId())))
                .with(a -> a.setCreatedBy(userService.getUserBySecContext()))
                .get();
        newAssertion = repository.save(newAssertion);
        Long parentId = newAssertion.getId();
        newAssertion.setChildren(dto.getChildren().stream().map(d -> {
            d.setParentId(parentId);
            return this.create(d);
        }).collect(Collectors.toSet()));
        return newAssertion;
    }

    @Transactional
    public Assertion updateById(Long id, UpdateAssertionDTO dto) {
        Assertion assertion = getObject(id);

        Set<Assertion> newChildren = dto.getChildren().stream().map(d -> {
            d.setParentId(id);
            return this.create(d);
        }).collect(Collectors.toSet());
        assertion.getChildren().addAll(newChildren);
        assertion.setStatus(dto.getStatus());
        assertion.setText(dto.getText());

        return repository.save(assertion);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Assertion assertionToDelete = getObject(id);
        assertionToDelete.getComments().forEach(c -> commentService.deleteById(c.getId()));
        assertionToDelete.getChildren().forEach(a -> deleteById(a.getId()));
        repository.deleteById(id);
    }

    public List<BlockerAssertionDTO> getBlockerAssertionsByProductId(Long productId) {
        var product = productService.findById(productId);
        List<Assertion> assertions = repository.findAll(AssertionSpecifications.hasProductId(productId)
                .and(AssertionSpecifications.hasStatus(AssertionStatus.BLOCKED)
                        .or(AssertionSpecifications.hasStatus(AssertionStatus.AT_RISK))));

        var blockers = convertAssertionsToBlockerAssertionDTOs(assertions);
        blockers.addAll(product.getChildren().stream().map(p ->
                getBlockerAssertionsByProductId(p.getId())).flatMap(List::stream).collect(Collectors.toList())
        );

        return blockers;
    }

    public List<BlockerAssertionDTO> getAllBlockerAssertions() {
        List<Assertion> assertions = repository.findAll(AssertionSpecifications.hasStatus(AssertionStatus.BLOCKED)
                .or(AssertionSpecifications.hasStatus(AssertionStatus.AT_RISK)));

        return this.convertAssertionsToBlockerAssertionDTOs(assertions);
    }

    protected List<BlockerAssertionDTO> convertAssertionsToBlockerAssertionDTOs(List<Assertion> assertions) {
        return assertions.stream().map(a -> {
            Comment latestComment = a.getComments().stream().max(Comparator.comparing(Comment::getId)).orElse(new Comment());
            latestComment.setChildren(Set.of());
            Product product = a.getProduct();
            Long productParentId = Optional.ofNullable(product.getParent()).map(Product::getId).orElse(null);
            a.setChildren(Set.of());
            a.setComments(Set.of());
            return new BlockerAssertionDTO(productParentId, product.getId(), product.getName(), a.toDto(), latestComment.toDto());
        }).collect(Collectors.toList());
    }
}
