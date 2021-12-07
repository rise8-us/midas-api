package mil.af.abms.midas.api.assertion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.assertion.dto.ArchiveAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.BlockerAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.helper.TimeConversion;
import mil.af.abms.midas.api.measure.Measure;
import mil.af.abms.midas.api.measure.MeasureService;
import mil.af.abms.midas.api.measure.dto.CreateMeasureDTO;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.CompletionType;
import mil.af.abms.midas.enums.ProgressionStatus;

@ExtendWith(SpringExtension.class)
@Import(AssertionService.class)
class AssertionServiceTests {

    @SpyBean
    private AssertionService assertionService;
    @MockBean
    private UserService userService;
    @MockBean
    private AssertionRepository assertionRepository;
    @MockBean
    private MeasureService measureService;
    @MockBean
    private ProductService productService;
    @MockBean
    CommentService commentService;
    @MockBean
    SimpMessageSendingOperations websocket;

    @Captor
    private ArgumentCaptor<Assertion> assertionCaptor;
    @Captor
    ArgumentCaptor<Long> longCaptor;

    private LocalDateTime CREATION_DATE;
    private User createdBy;
    private Product childProduct;
    private Comment childComment;
    private Assertion assertionSibling;

    private Product product;
    private Comment comment;
    private Measure measure;
    private Assertion assertion;
    private Assertion assertionChild;
    private Assertion assertionParent;
    private CreateAssertionDTO createAssertionDTO;
    private UpdateAssertionDTO updateAssertionDTO;
    private final LocalDate DUE_DATE = TimeConversion.getLocalDateOrNullFromObject("2021-07-09");

    @BeforeEach
    void beforeEach() {
        CREATION_DATE = LocalDateTime.now();
        createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();

        childProduct = Builder.build(Product.class)
                .with(p -> p.setId(5L))
                .with(p -> p.setName("Halo2")).get();
        childComment = Builder.build(Comment.class).with(c -> c.setId(204L)).with(c -> c.setCreatedBy(createdBy)).get();

        product = Builder.build(Product.class)
                .with(p -> p.setId(3L))
                .with(p -> p.setName("Halo"))
                .with(p -> p.setChildren(Set.of(childProduct)))
                .get();

        childProduct.setParent(product);

        comment = Builder.build(Comment.class).with(c -> c.setId(404L)).with(c -> c.setCreatedBy(createdBy)).get();
        assertion = Builder.build(Assertion.class)
                .with(a -> a.setId(3L))
                .get();
        measure = Builder.build(Measure.class)
                .with(m -> m.setId(1L))
                .with(m -> m.setValue(1F))
                .with(m -> m.setTarget(5F))
                .with(m -> m.setStartDate(DUE_DATE))
                .with(m -> m.setDueDate(DUE_DATE))
                .with(m -> m.setCompletionType(CompletionType.NUMBER))
                .with(m -> m.setAssertion(assertion))
                .with(m -> m.setText("First"))
                .with(m -> m.setComments(Set.of()))
                .get();
        assertionParent = Builder.build(Assertion.class)
                .with(a -> a.setId(1L))
                .with(a -> a.setProduct(product))
                .with(a -> a.setStatus(ProgressionStatus.ON_TRACK))
                .with(a -> a.setText("First"))
                .with(a -> a.setMeasures(Set.of(measure)))
                .with(a -> a.setComments(Set.of(comment)))
                .with(a -> a.setCreationDate(CREATION_DATE))
                .with(a -> a.setCreatedBy(createdBy))
                .get();
        assertionChild = Builder.build(Assertion.class)
                .with(a -> a.setId(2L))
                .with(a -> a.setParent(assertionParent))
                .with(a -> a.setProduct(product))
                .with(a -> a.setText("Strat"))
                .with(a -> a.setComments(Set.of(comment)))
                .with(a -> a.setStatus(ProgressionStatus.BLOCKED))
                .with(a -> a.setCreationDate(CREATION_DATE))
                .with(a -> a.setCreatedBy(createdBy))
                .get();
        assertionSibling = Builder.build(Assertion.class)
                .with(a -> a.setId(9L))
                .with(a -> a.setProduct(childProduct))
                .with(a -> a.setParent(assertionParent))
                .with(a -> a.setText("First"))
                .with(a -> a.setComments(Set.of(comment)))
                .with(a -> a.setStatus(ProgressionStatus.BLOCKED))
                .with(a -> a.setComments(Set.of(childComment)))
                .with(a -> a.setCreationDate(CREATION_DATE))
                .with(a -> a.setCreatedBy(createdBy))
                .get();

        createAssertionDTO = new CreateAssertionDTO("First",
                3L, null, null, new ArrayList<>(), null, new ArrayList<>(), null, null, "2020-01-01", "2020-02-02");
        updateAssertionDTO = new UpdateAssertionDTO("updated", ProgressionStatus.COMPLETED, List.of(createAssertionDTO), null, false, "2020-03-03", "2020-04-04");
    }

    @Test
    void should_create_assertion() {
        var createMeasureDTO = new CreateMeasureDTO();
        BeanUtils.copyProperties(measure, createMeasureDTO);
        var createChildAssertionDTO = new CreateAssertionDTO();
        BeanUtils.copyProperties(createAssertionDTO, createChildAssertionDTO);

        createAssertionDTO.setMeasures(List.of(createMeasureDTO));
        createAssertionDTO.setChildren(List.of(createChildAssertionDTO));

        when(productService.findById(3L)).thenReturn(product);
        when(userService.getUserBySecContext()).thenReturn(createdBy);
        when(assertionRepository.save(any())).thenReturn(assertionParent);
        when(measureService.create(any())).thenReturn(measure);
        when(assertionService.findByIdOrNull(any())).thenReturn(assertionParent);

        assertionService.create(createAssertionDTO);

        verify(assertionRepository, times(2)).save(assertionCaptor.capture());
        var assertionSaved = assertionCaptor.getValue();

        assertThat(assertionSaved.getText()).isEqualTo(createAssertionDTO.getText());
        assertThat(assertionSaved.getCreatedBy()).isEqualTo(createdBy);
        assertThat(assertionSaved.getStartDate()).isEqualTo(createAssertionDTO.getStartDate());
        assertThat(assertionSaved.getDueDate()).isEqualTo(createAssertionDTO.getDueDate());
    }

    @Test
    void should_update_assertion_by_id() {
        assertionParent.setChildren(Set.of(assertionChild));
        assertionChild.setStatus(ProgressionStatus.BLOCKED);

        var newAssertion = new Assertion();
        BeanUtils.copyProperties(assertionParent, newAssertion);
        newAssertion.setText("additional update");
        newAssertion.setId(10L);

        when(assertionRepository.findById(1L)).thenReturn(Optional.of(newAssertion));
        when(assertionRepository.save(newAssertion)).thenReturn(newAssertion);
        when(assertionRepository.save(assertionChild)).thenReturn(assertionChild);
        doNothing().when(assertionService).updateParentIfAllSiblingsComplete(any());
        doNothing().when(assertionService).updateChildrenToCompletedIfParentComplete(any());

        doReturn(newAssertion).when(assertionService).create(any());

        assertionService.updateById(1L, updateAssertionDTO);

        verify(assertionRepository, times(1)).save(assertionCaptor.capture());
        var assertionSaved = assertionCaptor.getValue();

        assertThat(assertionSaved.getText()).isEqualTo(updateAssertionDTO.getText());
        assertThat(assertionSaved.getStatus()).isEqualTo(updateAssertionDTO.getStatus());
        assertThat(assertionSaved.getStartDate()).isEqualTo(updateAssertionDTO.getStartDate());
        assertThat(assertionSaved.getDueDate()).isEqualTo(updateAssertionDTO.getDueDate());
    }

    @Test
    void should_delete_tree() {
        assertionParent.setChildren(Set.of(assertionChild));

        doReturn(assertionParent).when(assertionService).findById(1L);
        doReturn(assertionChild).when(assertionService).findById(2L);
        doNothing().when(assertionRepository).deleteById(1L);
        doNothing().when(assertionRepository).deleteById(2L);

        assertionService.deleteById(1L);

        verify(assertionRepository, times(2)).deleteById(longCaptor.capture());

        Long childId = longCaptor.getAllValues().get(0);
        Long parentId = longCaptor.getAllValues().get(1);

        assertThat(childId).isEqualTo(2L);
        assertThat(parentId).isEqualTo(1L);
    }

    @Test
    void should_recursively_deleteById() {
        assertionParent.getChildren().add(assertionChild);
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(5L)).get();

        doReturn(assertionParent).when(assertionService).findById(assertionParent.getId());
        doReturn(assertionChild).when(assertionService).findById(assertionChild.getId());
        doNothing().when(commentService).deleteComment(comment);
        doNothing().when(assertionRepository).deleteById(any());

        assertionService.deleteById(this.assertionParent.getId());

        verify(assertionRepository, times(2)).deleteById(longCaptor.capture());
        verify(commentService, times(2)).deleteComment(any());

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(2L);
        assertThat(longCaptor.getAllValues().get(1)).isEqualTo(1L);
    }

    @ParameterizedTest
    @CsvSource(value = {"true", "false"})
    void should_archive_assertion(boolean isArchived) {
        var archiveAssertionDTO = Builder.build(ArchiveAssertionDTO.class)
                .with(d -> d.setIsArchived(isArchived)).get();

        when(assertionRepository.findById(1L)).thenReturn(Optional.of(this.assertion));

        assertionService.archive(1L, archiveAssertionDTO);
        verify(assertionRepository).save(assertionCaptor.capture());

        assertThat(assertionCaptor.getValue().getIsArchived()).isEqualTo(isArchived);
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    void should_get_all_blocker_assertions() {
        Comment comment2 = new Comment();
        BeanUtils.copyProperties(comment, comment2);
        comment2.setId(501L);

        Assertion blockedAssertion = new Assertion();
        BeanUtils.copyProperties(assertionParent, blockedAssertion);
        blockedAssertion.setStatus(ProgressionStatus.BLOCKED);
        blockedAssertion.setComments(Set.of(comment, comment2));

        Assertion atRiskAssertion = new Assertion();
        BeanUtils.copyProperties(assertionParent, atRiskAssertion);
        atRiskAssertion.setStatus(ProgressionStatus.AT_RISK);

        List<Assertion> assertionList = List.of(blockedAssertion, atRiskAssertion);

        when(assertionRepository.findAll(any(Specification.class))).thenReturn(assertionList);

        List<Assertion> expectedAssertionList = assertionList.stream().map(a -> {
            Assertion assertion = new Assertion();
            BeanUtils.copyProperties(a, assertion);
            assertion.setComments(Set.of());
            assertion.setChildren(Set.of());

            return assertion;
        }).collect(Collectors.toList());

        List<BlockerAssertionDTO> expected = List.of(
                new BlockerAssertionDTO(null, product.getId(), product.getName(), expectedAssertionList.get(0).toDto(), comment2.toDto()),
                new BlockerAssertionDTO(null, product.getId(), product.getName(), expectedAssertionList.get(1).toDto(), comment.toDto())
        );

        assertThat(assertionService.getAllBlockerAssertions()).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"COMPLETED: true", "BLOCKED: false"}, delimiter = ':')
    void should_return_boolean_when_self_and_Siblings_are_completed(String status, boolean expected) {
        assertionChild.setStatus(ProgressionStatus.COMPLETED);
        assertionSibling.setStatus(ProgressionStatus.valueOf(status));
        assertionParent.setStatus(ProgressionStatus.ON_TRACK);
        assertionParent.setChildren(Set.of(assertionChild, assertionSibling));

        assertionService.updateParentIfAllSiblingsComplete(assertionSibling);

        assertThat(assertionParent.getStatus().equals(ProgressionStatus.COMPLETED)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource(value = {"COMPLETED: true", "BLOCKED: false"}, delimiter = ':')
    void should_return_boolean_when_parent_is_completed(String status, boolean expected) {
        assertionParent.setStatus(ProgressionStatus.valueOf(status));
        assertionParent.setChildren(Set.of(assertionChild));
        assertionService.updateChildrenToCompletedIfParentComplete(assertionParent);
        assertThat(assertionChild.getStatus().equals(ProgressionStatus.COMPLETED)).isEqualTo(expected);
    }
}
