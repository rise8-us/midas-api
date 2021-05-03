package mil.af.abms.midas.api.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Setter;

import mil.af.abms.midas.api.comment.CommentService;

public class CommentExistsValidator implements ConstraintValidator<CommentExists, Long> {

    @Autowired
    private CommentService commentService;

    @Setter
    private boolean allowNull;

    @Override
    public void initialize(CommentExists constraintAnnotation) { this.allowNull = constraintAnnotation.allowNull(); }

    @Override
    public boolean isValid(Long id, ConstraintValidatorContext constraintContext) {

        return id == null ? allowNull : commentService.existsById(id);
    }
}
