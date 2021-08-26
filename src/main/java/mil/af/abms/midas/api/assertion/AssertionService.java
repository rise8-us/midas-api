package mil.af.abms.midas.api.assertion;

import javax.transaction.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.assertion.dto.BlockerAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.AssertionStatus;

@Service
public class AssertionService extends AbstractCRUDService<Assertion, AssertionDTO, AssertionRepository> {

    private UserService userService;
    private ProductService productService;
    private CommentService commentService;
    private final SimpMessageSendingOperations websocket;

    public AssertionService(AssertionRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Assertion.class, AssertionDTO.class);
        this.websocket = websocket;
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
                .with(a -> a.setProduct(productService.findById(dto.getProductId())))
                .with(a -> a.setParent(findByIdOrNull(dto.getParentId())))
                .with(a -> a.setCreatedBy(userService.getUserBySecContext()))
                .with(a -> a.setAssignedPerson(userService.findByIdOrNull(dto.getAssignedPersonId())))
                .with(a -> a.setCompletionType(dto.getCompletionType()))
                .with(a -> a.setStartDate(TimeConversion.getTimeOrNull(dto.getStartDate())))
                .with(a -> a.setDueDate(TimeConversion.getTimeOrNull(dto.getDueDate())))
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
        Assertion assertion = findById(id);

        Set<Assertion> newChildren = dto.getChildren().stream().map(d -> {
            d.setParentId(id);
            return this.create(d);
        }).collect(Collectors.toSet());
        newChildren.addAll(assertion.getChildren());
        assertion.setChildren(newChildren);
        assertion.setStatus(dto.getStatus());
        assertion.setText(dto.getText());
        assertion.setAssignedPerson(userService.findByIdOrNull(dto.getAssignedPersonId()));
        assertion.setCompletionType(dto.getCompletionType());
        assertion.setStartDate(TimeConversion.getTimeOrNull(dto.getStartDate()));
        assertion.setDueDate(TimeConversion.getTimeOrNull(dto.getDueDate()));

        updateChildrenToCompletedIfParentComplete(assertion);
        updateParentIfAllSiblingsComplete(assertion);
        return repository.save(assertion);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Assertion assertionToDelete = findById(id);
        removeRelatedComments(assertionToDelete);
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
            Long productParentId = Optional.ofNullable(product).map(Product::getParent).map(Product::getId).orElse(null);
            a.setChildren(Set.of());
            a.setComments(Set.of());
            return new BlockerAssertionDTO(
                    productParentId,
                    a.getIdOrNull(product),
                    Optional.ofNullable(product).map(Product::getName).orElse(null),
                    a.toDto(), latestComment.toDto()
            );
        }).collect(Collectors.toList());
    }

    protected void removeRelatedComments(Assertion assertion) {
        assertion.getComments().forEach(commentService::deleteComment);
        assertion.setComments(Set.of());
        websocket.convertAndSend("/topic/update_assertion", assertion.toDto());
    }

    protected void updateParentIfAllSiblingsComplete(Assertion assertion) {
        if (assertion.getParent() == null ) return;
        var parent =  assertion.getParent();
        var isComplete = parent.getChildren().stream().filter(
                c -> c.getStatus().equals(AssertionStatus.COMPLETED)).count() == parent.getChildren().size();
       if (isComplete) {
           parent.setStatus(AssertionStatus.COMPLETED);
           updateParentIfAllSiblingsComplete(parent);
           repository.save(parent);
       }
    }

    protected void updateChildrenToCompletedIfParentComplete(Assertion assertion) {
        if (AssertionStatus.COMPLETED.equals(assertion.getStatus())) {
            assertion.getChildren().forEach(childAssertion -> {
            	childAssertion.setStatus(AssertionStatus.COMPLETED);
                updateChildrenToCompletedIfParentComplete(childAssertion);
                repository.save(childAssertion);
            });
        }
    }
}
