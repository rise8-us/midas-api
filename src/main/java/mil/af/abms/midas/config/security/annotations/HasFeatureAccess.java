package mil.af.abms.midas.config.security.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.security.access.prepost.PreAuthorize;

import mil.af.abms.midas.config.security.AuthExpression;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@PreAuthorize(AuthExpression.HAS_FEATURE_ACCESS)
public @interface HasFeatureAccess {
}
