package mil.af.abms.midas.api.assertion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

import org.springframework.beans.BeanUtils;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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

@ExtendWith(SpringExtension.class)
@Import(AssertionService.class)
public class AssertionServiceTests {
    
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
    
    @Captor
    private ArgumentCaptor<Assertion> assertionCaptor;
    @Captor ArgumentCaptor<Long> longCaptor;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Product product = Builder.build(Product.class)
            .with(p -> p.setId(3L)).get();
    private final Comment comment = Builder.build(Comment.class).with(c -> c.setId(404L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setProduct(product))
            .with(a -> a.setText("First"))
            .with(a -> a.setType(AssertionType.GOAL))
            .with(a -> a.setComments(Set.of(comment)))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setCreatedBy(createdBy)).get();
    private final Assertion assertionChild = Builder.build(Assertion.class)
            .with(a -> a.setId(2L))
            .with(a -> a.setProduct(product))
            .with(a -> a.setText("Strat"))
            .with(a -> a.setType(AssertionType.STRATEGY))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setCreatedBy(createdBy)).get();
    private final CreateAssertionDTO createAssertionDTOChild = new CreateAssertionDTO("Strat", AssertionType.STRATEGY,
            3L, null, null, new ArrayList<>());
    private final CreateAssertionDTO createAssertionDTO = new CreateAssertionDTO("First", AssertionType.GOAL,
            3L, null, null, List.of(createAssertionDTOChild));


    @Test
    public void should_create_assertion() {
        doReturn(assertion).when(assertionService).findByIdOrNull(1L);
        when(assertionRepository.save(any())).thenAnswer((new Answer<Assertion>() {
            private int count = 0;
            public Assertion answer(InvocationOnMock invocation) {
                count++;
                if (count == 1) {
                    return assertion;
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
    public void should_update_assertion_by_id() {
        UpdateAssertionDTO updateAssertionDTO = new UpdateAssertionDTO("updated", AssertionStatus.STARTED, List.of(createAssertionDTO));
        Assertion newAssertion = new Assertion();
        BeanUtils.copyProperties(assertion, newAssertion);
        newAssertion.setType(AssertionType.MEASURE);
        newAssertion.setText("additional update");

        when(assertionRepository.findById(1L)).thenReturn(Optional.of(newAssertion));
        when(assertionRepository.save(newAssertion)).thenReturn(newAssertion);
        doReturn(newAssertion).when(assertionService).create(any());

        assertionService.updateById(1L, updateAssertionDTO);

        verify(assertionRepository, times(1)).save(assertionCaptor.capture());
        Assertion assertionSaved = assertionCaptor.getValue();

        assertThat(assertionSaved.getText()).isEqualTo(updateAssertionDTO.getText());
        assertThat(assertionSaved.getStatus()).isEqualTo(updateAssertionDTO.getStatus());
    }

    @Test
    public void should_delete_tree() {
        Assertion assertionParent = new Assertion();
        BeanUtils.copyProperties(assertion, assertionParent);
        Assertion assertionChild = Builder.build(Assertion.class).with(a -> a.setId(2L)).get();
        assertionParent.setChildren(Set.of(assertionChild));

        doReturn(assertionParent).when(assertionService).getObject(1L);
        doReturn(assertionChild).when(assertionService).getObject(2L);
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
    public void should_recursively_deleteById() {
        Assertion parentAssertion = new Assertion();
        BeanUtils.copyProperties(assertion, parentAssertion);
        Assertion childAssertion = Builder.build(Assertion.class)
                .with(a -> a.setId(4L))
                .with(a -> a.setParent(parentAssertion))
                .get();
        parentAssertion.getChildren().add(childAssertion);
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(5L)).get();

        doReturn(parentAssertion).when(assertionService).getObject(1L);
        doReturn(childAssertion).when(assertionService).getObject(4L);
        doNothing().when(commentService).deleteById(5L);
        doNothing().when(assertionRepository).deleteById(any());

        assertionService.deleteById(assertion.getId());

        verify(assertionRepository, times(2)).deleteById(longCaptor.capture());
        verify(commentService, times(1)).deleteById(any());

        assertThat(longCaptor.getAllValues().get(0)).isEqualTo(4L);
        assertThat(longCaptor.getAllValues().get(1)).isEqualTo(1L);
    }

}
