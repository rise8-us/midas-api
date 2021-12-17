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
import mil.af.abms.midas.api.comment.dto.CreateCommentDTO;
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

    public AssertionService(AssertionRepository repository, SimpMessageSendingOperations websocket) {
        super(repository, Assertion.class, AssertionDTO.class);
        this.websocket = websocket;
    }

    @Autowired
    public void setUserService(UserService userService) { this.userService = userService; }
    @Autowired
    public void setProductService(ProductService productService) { this.productService = productService; }
    @Autowired
    public void setCommentService(CommentService commentService) { this.commentService = commentService; }
    @Autowired
    public void setMeasureService(MeasureService measureService) { this.measureService = measureService; }

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
        var assertionCreated = repository.save(newAssertion);

        createChildAssertions(assertionCreated, dto.getChildren());
        createMeasures(assertionCreated, dto.getMeasures());
        sendParentUpdatedWebsocketMessage(assertionCreated, true);

        return assertionCreated;
    }

    @Transactional
    public Assertion updateById(Long id, UpdateAssertionDTO dto) {
        var assertionToUpdate = findById(id);

        assertionToUpdate.setStatus(dto.getStatus());
        assertionToUpdate.setText(dto.getText());
        assertionToUpdate.setAssignedPerson(userService.findByIdOrNull(dto.getAssignedPersonId()));
        assertionToUpdate.setStartDate(getLocalDateOrNullFromObject(dto.getStartDate()));
        assertionToUpdate.setDueDate(getLocalDateOrNullFromObject(dto.getDueDate()));

        addNewChildren(assertionToUpdate, dto);
        calculateCompleted(assertionToUpdate, dto);
        assertionToUpdate.getMeasures().forEach((measure) -> {
            measureService.updateMeasureIfAssertionComplete(measure, assertionToUpdate.getStatus(), assertionToUpdate.getText());
        });
        updateChildrenToCompletedIfParentComplete(assertionToUpdate);
        updateAssertionIfAllChildrenAndMeasuresComplete(assertionToUpdate.getParent());

        return repository.save(assertionToUpdate);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        var assertionToDelete = findById(id);
        sendParentUpdatedWebsocketMessage(assertionToDelete, false);
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

    public void updateAssertionIfAllChildrenAndMeasuresComplete(Assertion assertion) {
        if (assertion == null) return;
        var isComplete = (assertion.getChildren().stream().filter(
                c -> c.getStatus().equals(ProgressionStatus.COMPLETED)).count() == assertion.getChildren().size() &&
                    assertion.getMeasures().stream().filter(
                m -> m.getStatus().equals(ProgressionStatus.COMPLETED)).count() == assertion.getMeasures().size());
        if (assertion.getStatus() != ProgressionStatus.COMPLETED && isComplete) {
            assertion.setStatus(ProgressionStatus.COMPLETED);
            assertion.setCompletedAt(LocalDateTime.now());
            var userName = userService.getUserDisplayNameOrUsername();
            commentService.create(new CreateCommentDTO(
                    null,
                    assertion.getId(),
                    null,
                    String.format("%s marked all requirements as completed, marking \"%s\" as complete!###COMPLETED", userName, assertion.getText())
            ), true);
            repository.save(assertion);
        }
        updateAssertionIfAllChildrenAndMeasuresComplete(assertion.getParent());
    }

    protected void updateChildrenToCompletedIfParentComplete(Assertion assertion) {
        if (ProgressionStatus.COMPLETED.equals(assertion.getStatus())) {
            assertion.getChildren().forEach(childAssertion -> {
                if (childAssertion.getStatus() != ProgressionStatus.COMPLETED) {
                    childAssertion.setStatus(ProgressionStatus.COMPLETED);
                    childAssertion.setCompletedAt(LocalDateTime.now());
                    var userName = userService.getUserDisplayNameOrUsername();
                    commentService.create(new CreateCommentDTO(
                            null,
                            childAssertion.getId(),
                            null,
                            String.format("%s marked \"%s\" as completed, marking \"%s\" as complete!###COMPLETED", userName, assertion.getText(), childAssertion.getText())
                    ), true);
                    repository.save(childAssertion);
                    childAssertion.getMeasures().forEach((measure) -> {
                        measureService.updateMeasureIfAssertionComplete(measure, childAssertion.getStatus(), childAssertion.getText());
                    });
                }
                updateChildrenToCompletedIfParentComplete(childAssertion);
            });
        }
    }

    private void createChildAssertions(Assertion parentAssertion, List<CreateAssertionDTO> dtos) {
        var children = dtos.stream().map(d -> {
                d.setParentId(parentAssertion.getId());
                return this.create(d);
            }).collect(Collectors.toSet());
        parentAssertion.setChildren(children);
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
        assertion.getComments().forEach(commentService::deleteAllRelatedComments);
        assertion.setComments(Set.of());
    }

    private void createMeasures(Assertion owningAssertion, List<CreateMeasureDTO> dtos) {
       var measures = dtos.stream().map(d -> {
            d.setAssertionId(owningAssertion.getId());
            return measureService.create(d);
        }).collect(Collectors.toSet());
       owningAssertion.setMeasures(measures);
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

    private void calculateCompleted(Assertion assertion, UpdateAssertionDTO dto) {
        if (assertion.getCompletedAt() == null && dto.getStatus() == ProgressionStatus.COMPLETED) {
            assertion.setCompletedAt(LocalDateTime.now());
        } else if (assertion.getCompletedAt() != null && dto.getStatus() != ProgressionStatus.COMPLETED) {
            assertion.setCompletedAt(null);
        }
    }

    private void addNewChildren(Assertion assertion, UpdateAssertionDTO dto) {
        var newChildren = dto.getChildren().stream().map(d -> {
            d.setParentId(assertion.getId());
            return this.create(d);
        }).collect(Collectors.toSet());
        newChildren.addAll(assertion.getChildren());
        assertion.setChildren(newChildren);
    }

}
