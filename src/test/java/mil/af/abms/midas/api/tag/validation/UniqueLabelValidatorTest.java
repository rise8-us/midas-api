package mil.af.abms.midas.api.tag.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.tag.Tag;
import mil.af.abms.midas.api.tag.TagService;
import mil.af.abms.midas.exception.EntityNotFoundException;
import mil.af.abms.midas.helpers.RequestContext;

@ExtendWith(SpringExtension.class)
@Import({UniqueLabelValidator.class})
public class UniqueLabelValidatorTest {

    private final Tag foundTag = Builder.build(Tag.class)
            .with(t -> t.setId(1L))
            .with(t -> t.setLabel("Test Label"))
            .with(t -> t.setDescription("Test Description"))
            .with(t -> t.setColor("#969696")).get();

    @Autowired
    UniqueLabelValidator validator;
    @MockBean
    private TagService tagService;
    @Mock
    private ConstraintValidatorContext context;
    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    public void init() {
        when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(builder);
    }

    @AfterEach
    public void tearDown() {
        clearRequestContext();
    }

    @Test
    public void should_validate_new_tag_true() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(true);

        when(tagService.findByLabel(foundTag.getLabel())).thenThrow(new EntityNotFoundException("Tag"));

        assertTrue(validator.isValid(foundTag.getLabel(), context));
    }

    @Test
    public void should_validate_new_tag_false() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(true);

        when(tagService.findByLabel(any())).thenReturn(foundTag);

        assertFalse(validator.isValid(foundTag.getLabel(), context));
    }

    @Test
    public void should_validate_update_Tag_true() {
        RequestContext.setRequestContext("id", "1");
        validator.setNew(false);

        when(tagService.findByLabel(any())).thenReturn(foundTag);

        assertTrue(validator.isValid(foundTag.getLabel(), context));
    }

    @Test
    public void should_validate_update_Tag_false() {
        RequestContext.setRequestContext("id", "2");
        validator.setNew(false);

        when(tagService.findByLabel(any())).thenReturn(foundTag);

        assertFalse(validator.isValid(foundTag.getLabel(), context));
    }

    private void clearRequestContext() {
        RequestContextHolder.resetRequestAttributes();
    }
}
