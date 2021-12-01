package mil.af.abms.midas.api.assertion;

import static mil.af.abms.midas.api.helper.TimeConversion.getLocalDateOrNullFromObject;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mil.af.abms.midas.api.AbstractCRUDService;
import mil.af.abms.midas.api.assertion.dto.ArchiveAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.assertion.dto.BlockerAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.measure.MeasureService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.ProgressionStatus;

@Service
public class AssertionService extends AbstractCRUDService<Assertion, AssertionDTO, AssertionRepository> {

    private UserService userService;
    private ProductService productService;
    private CommentService commentService;
    private MeasureService measureService;

    public AssertionService(AssertionRepository repository) { super(repository, Assertion.class, AssertionDTO.class); }

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

    @Autowired
    public void setMeasureService(MeasureService measureService) {
        this.measureService = measureService;
    }

    @Transactional
    public Assertion create(CreateAssertionDTO dto) {
        Assertion newAssertion = Builder.build(Assertion.class)
                .with(a -> a.setText(dto.getText()))
                .with(a -> a.setStatus(ProgressionStatus.NOT_STARTED))
                .with(a -> a.setProduct(productService.findById(dto.getProductId())))
                .with(a -> a.setParent(findByIdOrNull(dto.getParentId())))
                .with(a -> a.setInheritedFrom(findByIdOrNull(dto.getInheritedFromId())))
                .with(a -> a.setCreatedBy(userService.getUserBySecContext()))
                .with(a -> a.setAssignedPerson(userService.findByIdOrNull(dto.getAssignedPersonId())))
                .with(a -> a.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate())))
                .with(a -> a.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate())))
                .get();
        var savedAssertion = repository.save(newAssertion);

        Long assertionId = savedAssertion.getId();
        savedAssertion.setChildren(dto.getChildren().stream().map(d -> {
            d.setParentId(assertionId);
            return this.create(d);
        }).collect(Collectors.toSet()));
        savedAssertion.setMeasures(dto.getMeasures().stream().map(d -> {
            d.setAssertionId(assertionId);
            return measureService.create(d);
        }).collect(Collectors.toSet()));
        return savedAssertion;
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
        assertion.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate()));
        assertion.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate()));

        if (assertion.getCompletedAt() == null && dto.getStatus() == ProgressionStatus.COMPLETED) {
            assertion.setCompletedAt(LocalDateTime.now());
        }

        updateChildrenToCompletedIfParentComplete(assertion);
        updateParentIfAllSiblingsComplete(assertion);
        return repository.save(assertion);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        Assertion assertionToDelete = findById(id);
        removeRelatedMeasures(assertionToDelete);
        removeRelatedComments(assertionToDelete);
        assertionToDelete.getChildren().forEach(a -> deleteById(a.getId()));
        repository.deleteById(id);
    }

    @Transactional
    public Assertion archive(Long id, ArchiveAssertionDTO archiveAssertionDTO) {
        var assertionToArchive = findById(id);
        assertionToArchive.setIsArchived(archiveAssertionDTO.getIsArchived());
        return repository.save(assertionToArchive);
    }

    public List<BlockerAssertionDTO> getAllBlockerAssertions() {
        List<Assertion> assertions = repository.findAll(AssertionSpecifications.hasStatus(ProgressionStatus.BLOCKED)
                .or(AssertionSpecifications.hasStatus(ProgressionStatus.AT_RISK)));

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

    protected void removeRelatedMeasures(Assertion assertion) {
        assertion.getMeasures().forEach(measureService::deleteMeasure);
        assertion.setMeasures(Set.of());
    }

    protected void removeRelatedComments(Assertion assertion) {
        assertion.getComments().forEach(commentService::deleteComment);
        assertion.setComments(Set.of());
    }

    protected void updateParentIfAllSiblingsComplete(Assertion assertion) {
        if (assertion.getParent() == null) return;
        var parent =  assertion.getParent();
        var isComplete = parent.getChildren().stream().filter(
                c -> c.getStatus().equals(ProgressionStatus.COMPLETED)).count() == parent.getChildren().size();
       if (isComplete) {
           parent.setStatus(ProgressionStatus.COMPLETED);
           updateParentIfAllSiblingsComplete(parent);
           repository.save(parent);
       }
    }

    protected void updateChildrenToCompletedIfParentComplete(Assertion assertion) {
        if (ProgressionStatus.COMPLETED.equals(assertion.getStatus())) {
            assertion.getChildren().forEach(childAssertion -> {
                childAssertion.setStatus(ProgressionStatus.COMPLETED);
                updateChildrenToCompletedIfParentComplete(childAssertion);
                repository.save(childAssertion);
            });
        }
    }

}
