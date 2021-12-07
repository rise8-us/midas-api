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
import org.springframework.messaging.simp.SimpMessageSendingOperations;
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
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.ProgressionStatus;

@Service
public class AssertionService extends AbstractCRUDService<Assertion, AssertionDTO, AssertionRepository> {

    private final SimpMessageSendingOperations websocket;

    private UserService userService;
    private ProductService productService;
    private CommentService commentService;
    private MeasureService measureService;
    private Assertion assertion;

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
        assertion = repository.save(newAssertion);

        createChildAssertions(assertion.getId(), dto.getChildren());
        createMeasures(assertion.getId(), dto.getMeasures());
        sendParentUpdatedWebsocketMessage(assertion, true);

        return assertion;
    }

    @Transactional
    public Assertion updateById(Long id, UpdateAssertionDTO dto) {
        this.assertion = findById(id);
        this.assertion.setStatus(dto.getStatus());
        this.assertion.setText(dto.getText());
        this.assertion.setAssignedPerson(userService.findByIdOrNull(dto.getAssignedPersonId()));
        this.assertion.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate()));
        this.assertion.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate()));

        addNewChildren(dto);
        calculateCompleted(dto);
        updateChildrenToCompletedIfParentComplete(this.assertion);
        updateParentIfAllSiblingsComplete(this.assertion);

        return repository.save(this.assertion);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        this.assertion = findById(id);
        sendParentUpdatedWebsocketMessage(this.assertion, false);
        removeRelatedMeasures(this.assertion);
        removeRelatedComments(this.assertion);
        this.assertion.getChildren().forEach(a -> deleteById(a.getId()));
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

    private void createChildAssertions(Long parentId, List<CreateAssertionDTO> dtos) {
      assertion.setChildren(dtos.stream().map(d -> {
            d.setParentId(parentId);
            return this.create(d);
        }).collect(Collectors.toSet()));
    }


    private List<BlockerAssertionDTO> convertAssertionsToBlockerAssertionDTOs(List<Assertion> assertions) {
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

    private void removeRelatedMeasures(Assertion assertion) {
        assertion.getMeasures().forEach(measureService::deleteMeasure);
        assertion.setMeasures(Set.of());
    }

    private void removeRelatedComments(Assertion assertion) {
        assertion.getComments().forEach(commentService::deleteComment);
        assertion.setComments(Set.of());
    }


    private void createMeasures(Long parentId, List<CreateMeasureDTO> dtos) {
        assertion.setMeasures(dtos.stream().map(d -> {
            d.setAssertionId(parentId);
            return measureService.create(d);
        }).collect(Collectors.toSet()));
    }

    private void sendParentUpdatedWebsocketMessage(Assertion assertion, boolean isAdded) {
        Optional.ofNullable(assertion.getParent()).ifPresent(parent -> {
            if (isAdded) { parent.getChildren().add(assertion); }
            else {
                parent.setChildren(parent.getChildren().stream()
                            .filter(a -> !a.getId().equals(assertion.getId()))
                            .collect(Collectors.toSet())
                );
            }
            websocket.convertAndSend("/topic/update_assertion", parent.toDto());
        });
    }

    private void calculateCompleted(UpdateAssertionDTO dto) {
        if (assertion.getCompletedAt() == null && dto.getStatus() == ProgressionStatus.COMPLETED) {
            assertion.setCompletedAt(LocalDateTime.now());
        }
    }

    private void addNewChildren(UpdateAssertionDTO dto) {
        var newChildren = dto.getChildren().stream().map(d -> {
            d.setParentId(assertion.getId());
            return this.create(d);
        }).collect(Collectors.toSet());
        newChildren.addAll(assertion.getChildren());
        assertion.setChildren(newChildren);
    }

}
