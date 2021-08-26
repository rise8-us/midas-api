package mil.af.abms.midas.api.assertion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import mil.af.abms.midas.api.assertion.dto.AssertionDTO;
import mil.af.abms.midas.api.assertion.dto.BlockerAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.AssertionStatus;
import mil.af.abms.midas.enums.AssertionType;
import mil.af.abms.midas.enums.CompletionType;

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
    private Assertion assertionChild;
    private Assertion assertionParent;
    private CreateAssertionDTO createAssertionDTOChild;
    private CreateAssertionDTO createAssertionDTO;

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
                .with(p -> p.setChildren(Set.of(childProduct))).get();

        childProduct.setParent(product);

        comment = Builder.build(Comment.class).with(c -> c.setId(404L)).with(c -> c.setCreatedBy(createdBy)).get();
        assertionParent = Builder.build(Assertion.class)
                .with(a -> a.setId(1L))
                .with(a -> a.setProduct(product))
                .with(a -> a.setStatus(AssertionStatus.ON_TRACK))
                .with(a -> a.setText("First"))
                .with(a -> a.setType(AssertionType.GOAL))
                .with(a -> a.setComments(Set.of(comment)))
                .with(a -> a.setCreationDate(CREATION_DATE))
                .with(a -> a.setCreatedBy(createdBy)).get();
        assertionChild = Builder.build(Assertion.class)
                .with(a -> a.setId(2L))
                .with(a -> a.setParent(assertionParent))
                .with(a -> a.setProduct(product))
                .with(a -> a.setText("Strat"))
                .with(a -> a.setStatus(AssertionStatus.BLOCKED))
                .with(a -> a.setType(AssertionType.STRATEGY))
                .with(a -> a.setCreationDate(CREATION_DATE))
                .with(a -> a.setCreatedBy(createdBy)).get();
        assertionSibling = Builder.build(Assertion.class)
                .with(a -> a.setId(9L))
                .with(a -> a.setProduct(childProduct))
                .with(a -> a.setParent(assertionParent))
                .with(a -> a.setText("First"))
                .with(a -> a.setType(AssertionType.GOAL))
                .with(a -> a.setStatus(AssertionStatus.BLOCKED))
                .with(a -> a.setComments(Set.of(childComment)))
                .with(a -> a.setCreationDate(CREATION_DATE))
                .with(a -> a.setCreatedBy(createdBy)).get();

        createAssertionDTOChild = new CreateAssertionDTO("Strat", AssertionType.STRATEGY,
                3L, null, null, new ArrayList<>(), null, null, null, null, null);
        createAssertionDTO = new CreateAssertionDTO("First", AssertionType.GOAL,
                3L, null, null, List.of(createAssertionDTOChild), null, null, CompletionType.STRING, null, null);
    }

    @Test
    void should_create_assertion() {
        doReturn(assertionParent).when(assertionService).findByIdOrNull(1L);
        when(assertionRepository.save(any())).thenAnswer((new Answer<Assertion>() {
            private int count = 0;

            public Assertion answer(InvocationOnMock invocation) {
                count++;
                if (count == 1) {
                    return assertionParent;
                }
                return assertionChild;

            }
        }));
        when(userService.getUserBySecContext()).thenReturn(createdBy);

        assertionService.create(createAssertionDTO);

        verify(assertionRepository, times(2)).save(assertionCaptor.capture());
        Assertion assertionSaved = assertionCaptor.getAllValues().get(0);

        assertThat(assertionSaved.getText()).isEqualTo(createAssertionDTO.getText());
        assertThat(assertionSaved.getType()).isEqualTo(createAssertionDTO.getType());
        assertThat(assertionSaved.getCreatedBy()).isEqualTo(createdBy);
    }

    @Test
    void should_update_assertion_by_id() {
        var updateAssertionDTO = new UpdateAssertionDTO("updated", AssertionStatus.COMPLETED, List.of(createAssertionDTO), null, null, null, null, null, false);
        assertionParent.setChildren(Set.of(assertionChild));
        assertionChild.setStatus(AssertionStatus.BLOCKED);
        var newAssertion = new Assertion();
        BeanUtils.copyProperties(assertionParent, newAssertion);
        newAssertion.setType(AssertionType.MEASURE);
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

    }

    @Test
    void should_delete_tree() {
        Assertion assertionParent = new Assertion();
        BeanUtils.copyProperties(this.assertionParent, assertionParent);
        Assertion assertionChild = Builder.build(Assertion.class).with(a -> a.setId(2L)).get();
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
        Assertion parentAssertion = new Assertion();
        BeanUtils.copyProperties(this.assertionParent, parentAssertion);
        Assertion childAssertion = Builder.build(Assertion.class)
                .with(a -> a.setId(4L))
                .with(a -> a.setParent(parentAssertion))
                .get();
        parentAssertion.getChildren().add(childAssertion);
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(5L)).get();

        doReturn(parentAssertion).when(assertionService).findById(parentAssertion.getId());
        doReturn(childAssertion).when(assertionService).findById(childAssertion.getId());
        doNothing().when(commentService).deleteComment(comment);
        doNothing().when(assertionRepository).deleteById(any());

        assertionService.deleteById(this.assertionParent.getId());

        verify(assertionRepository, times(2)).deleteById(longCaptor.capture());
        verify(commentService, times(1)).deleteComment(any());
        verify(websocket, times(2)).convertAndSend(anyString(), any(AssertionDTO.class));

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(4L);
        assertThat(longCaptor.getAllValues().get(1)).isEqualTo(1L);
    }

    @Test
    @SuppressWarnings(value = "unchecked")
    void should_get_all_blocker_assertions() {
        Comment comment2 = new Comment();
        BeanUtils.copyProperties(comment, comment2);
        comment2.setId(501L);

        Assertion blockedAssertion = new Assertion();
        BeanUtils.copyProperties(assertionParent, blockedAssertion);
        blockedAssertion.setStatus(AssertionStatus.BLOCKED);
        blockedAssertion.setComments(Set.of(comment, comment2));

        Assertion atRiskAssertion = new Assertion();
        BeanUtils.copyProperties(assertionParent, atRiskAssertion);
        atRiskAssertion.setStatus(AssertionStatus.AT_RISK);

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
        assertionChild.setStatus(AssertionStatus.COMPLETED);
        assertionSibling.setStatus(AssertionStatus.valueOf(status));
        assertionParent.setStatus(AssertionStatus.ON_TRACK);
        assertionParent.setChildren(Set.of(assertionChild, assertionSibling));

        assertionService.updateParentIfAllSiblingsComplete(assertionSibling);

        assertThat(assertionParent.getStatus().equals(AssertionStatus.COMPLETED)).isEqualTo(expected);
    }
}