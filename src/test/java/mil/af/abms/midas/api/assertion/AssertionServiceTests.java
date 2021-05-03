package mil.af.abms.midas.api.assertion;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import mil.af.abms.midas.api.assertion.dto.CreateAssertionDTO;
import mil.af.abms.midas.api.assertion.dto.UpdateAssertionDTO;
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.ogsm.OgsmService;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.enums.AssertionType;

@ExtendWith(SpringExtension.class)
@Import(AssertionService.class)
public class AssertionServiceTests {
    
    @Autowired
    private AssertionService assertionService;
    @MockBean
    private UserService userService;
    @MockBean
    private TagService tagService;
    @MockBean
    private AssertionRepository assertionRepository;
    @MockBean
    private OgsmService ogsmService;
    @MockBean
    private CommentService commentService;
    
    @Captor
    private ArgumentCaptor<Assertion> assertionCaptor;

    private final LocalDateTime CREATION_DATE = LocalDateTime.now();
    private final Set<Comment> comments = Set.of(Builder.build(Comment.class).with(c -> c.setId(2L)).get());
    private final Tag tagInAssertion = Builder.build(Tag.class)
            .with(t -> t.setId(22L))
            .with(t -> t.setLabel("TagInAssertion")).get();
    private final Tag tagTwoInAssertion = Builder.build(Tag.class)
            .with(t -> t.setId(21L))
            .with(t -> t.setLabel("TagTwoInAssertion")).get();
    private final Tag tag = Builder.build(Tag.class).with(t -> t.setId(2L)).get();
    private final User createdBy = Builder.build(User.class).with(u -> u.setId(3L)).get();
    private final Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(1L))
            .with(a -> a.setText("First"))
            .with(a -> a.setType(AssertionType.OBJECTIVE))
            .with(a -> a.setCreationDate(CREATION_DATE))
            .with(a -> a.setComments(comments))
            .with(a -> a.setTags(Set.of(tagInAssertion, tagTwoInAssertion)))
            .with(a -> a.setCreatedBy(createdBy)).get();
    
    @Test
    public void should_create_assertion() {
        CreateAssertionDTO createAssertionDTO = new CreateAssertionDTO("First", AssertionType.OBJECTIVE,  1L,
                Set.of(2L));
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(25L)).get();

        when(assertionRepository.save(assertion)).thenReturn(new Assertion());
        when(userService.getUserBySecContext()).thenReturn(createdBy);
        when(tagService.getObject(anyLong())).thenReturn(tag);
        when(commentService.getObject(anyLong())).thenReturn(comment);

        assertionService.create(createAssertionDTO);

        verify(assertionRepository, times(1)).save(assertionCaptor.capture());
        Assertion assertionSaved = assertionCaptor.getValue();

        assertThat(assertionSaved.getText()).isEqualTo(createAssertionDTO.getText());
        assertThat(assertionSaved.getType()).isEqualTo(createAssertionDTO.getType());
        assertThat(assertionSaved.getCreatedBy()).isEqualTo(createdBy);
        assertThat(assertionSaved.getTags()).isEqualTo(Set.of(tag));
    }

    @Test
    public void should_update_assertion_by_id() {
        UpdateAssertionDTO updateAssertionDTO = new UpdateAssertionDTO("updated", AssertionType.OBJECTIVE, Set.of(2L), Set.of(2L));
        Assertion newAssertion = new Assertion();
        BeanUtils.copyProperties(assertion, newAssertion);
        newAssertion.setType(AssertionType.MEASURE);
        newAssertion.setText("additional update");

        when(assertionRepository.findById(1L)).thenReturn(Optional.of(assertion));
        when(assertionRepository.save(assertion)).thenReturn(assertion);

        assertionService.updateById(1L, updateAssertionDTO);

        verify(assertionRepository, times(1)).save(assertionCaptor.capture());
        Assertion assertionSaved = assertionCaptor.getValue();

        assertThat(assertionSaved.getText()).isEqualTo(updateAssertionDTO.getText());
        assertThat(assertionSaved.getType()).isEqualTo(updateAssertionDTO.getType());
    }

    @Test
    public void should_set_tag_to_empty_set() {
        Comment comment = Builder.build(Comment.class).with(c -> c.setId(25L)).get();
        UpdateAssertionDTO updateDTO = Builder.build(UpdateAssertionDTO.class)
                .with(d -> d.setTagIds(Set.of()))
                .with(d -> d.setCommentIds(Set.of(comment.getId()))).get();

        when(assertionRepository.findById(1L)).thenReturn(Optional.of(assertion));
        when(commentService.getObject(anyLong())).thenReturn(comment);

        assertionService.updateById(1L, updateDTO);

        verify(assertionRepository, times(1)).save(assertionCaptor.capture());
        Assertion assertionSaved = assertionCaptor.getValue();

        assertThat(assertionSaved.getTags()).isEqualTo(Set.of());
    }

    @Test
    public void should_remove_tag_from_assertions() {
        assertionService.removeTagFromAssertions(tagInAssertion.getId(), Set.of(assertion));

        verify(assertionRepository, times(1)).save(assertionCaptor.capture());
        Assertion assertionSaved = assertionCaptor.getValue();
        assertThat(assertionSaved.getTags()).isEqualTo(Set.of(tagTwoInAssertion));
    }

    @Test
    public void should_remove_tag_from_assertion() {
        assertionService.removeTagFromAssertion(tagInAssertion.getId(), assertion);

        Set<Tag> tagsToKeep = Set.of(tagTwoInAssertion);

        verify(assertionRepository, times(1)).save(assertionCaptor.capture());
        Assertion assertionSaved = assertionCaptor.getValue();

        assertThat(assertionSaved.getTags()).isEqualTo(tagsToKeep);
    }
    
}
