package mil.af.abms.midas.config.security;

import java.util.Optional;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.feature.FeatureService;
import mil.af.abms.midas.api.feedback.FeedbackService;
import mil.af.abms.midas.api.measure.Measure;
import mil.af.abms.midas.api.measure.MeasureService;
import mil.af.abms.midas.api.persona.PersonaService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.config.SpringContext;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private static AssertionService assertionService() { return SpringContext.getBean(AssertionService.class); }
    private static MeasureService measureService() { return SpringContext.getBean(MeasureService.class); }
    private static ProductService productService() { return SpringContext.getBean(ProductService.class); }
    private static UserService userService() { return SpringContext.getBean(UserService.class); }
    private static ProjectService projectService() { return SpringContext.getBean(ProjectService.class); }
    private static CommentService commentService() { return SpringContext.getBean(CommentService.class); }
    private static PersonaService personaService() { return SpringContext.getBean(PersonaService.class); }
    private static FeatureService featureService() { return SpringContext.getBean(FeatureService.class); }
    private static EpicService epicService() { return SpringContext.getBean(EpicService.class); }
    private static FeedbackService feedbackService() { return SpringContext.getBean(FeedbackService.class); }

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean isSelf(Long userToModifyId) {
        var userToModify = userService().findById(userToModifyId);
        var userMakingRequest = userService().getUserBySecContext();
        return userMakingRequest.equals(userToModify);
    }

    public boolean hasTeamAccess(Long teamToModifyId) {
        if (teamToModifyId == null) { return false; }
        var userMakingRequest = userService().getUserBySecContext();
        return userMakingRequest.getTeamIds().contains(teamToModifyId);
    }

    public boolean hasProjectAccess(Long projectId) {
        var projectBeingAccessed = projectService().findById(projectId);
        var productContainingProject = Optional.ofNullable(projectBeingAccessed.getProduct()).orElse(new Product());
        var teamId = Optional.ofNullable(projectBeingAccessed.getTeam()).map(Team::getId).orElse(null);
        var productId = productContainingProject.getId();
        return hasTeamAccess(teamId) || hasProductAccess(productId);
    }

    public boolean hasProductAccess(Long productId) {
        if (productId == null) { return false; }
        var productBeingAccessed = productService().findById(productId);
        var parent = Optional.ofNullable(productBeingAccessed.getParent()).orElse(new Product());
        var userMakingRequest = userService().getUserBySecContext();
        var productManager = productBeingAccessed.getOwner();
        var teamId = productBeingAccessed.getTeams().stream().findFirst().map(Team::getId).orElse(null);
        return userMakingRequest.equals(productManager) || hasProductAccess(parent.getId()) || hasTeamAccess(teamId);
    }

    public boolean hasAssertionWriteAccess(Long assertionId) {
        if (assertionId == null) { return false; }
        Assertion assertionBeingAccessed = assertionService().findById(assertionId);
        Long productId = Optional.ofNullable(assertionBeingAccessed.getProduct()).map(Product::getId).orElse(null);
        return hasProductAccess(productId);
    }

    public boolean hasMeasureWriteAccess(Long measureId) {
        if (measureId == null) { return false; }
        Measure measureBeingAccessed = measureService().findById(measureId);
        Long assertionId = Optional.ofNullable(measureBeingAccessed.getAssertion()).map(Assertion::getId).orElse(null);
        return hasAssertionWriteAccess(assertionId);
    }

    public boolean isCommentCreator(Long commentId) {
        if (commentId == null) { return false; }
        var commentToModify = commentService().findById(commentId);
        var userMakingRequest = userService().getUserBySecContext();
        return userMakingRequest.getId().equals(commentToModify.getCreatedBy().getId());
    }

    public boolean isFeedbackCreator(Long feedbackId) {
        if (feedbackId == null) { return false; }
        var feedbackToModify = feedbackService().findById(feedbackId);
        var userMakingRequest = userService().getUserBySecContext();
        return userMakingRequest.getId().equals(feedbackToModify.getCreatedBy().getId());
    }

    public boolean hasPersonaUpdateAccess(Long personaId) {
        if (personaId == null) { return false; }
        var personaBeingAccessed = personaService().findById(personaId);
        if (personaBeingAccessed.getProduct() == null) { return false; }
        return hasProductAccess(personaBeingAccessed.getProduct().getId());
    }

    public boolean hasFeatureUpdateAccess(Long featureId) {
        if (featureId == null) { return false; }
        var featureBeingAccessed = featureService().findById(featureId);
        if (featureBeingAccessed.getProduct() == null) { return false; }
        return hasProductAccess(featureBeingAccessed.getProduct().getId());
    }

    public boolean hasEpicHideAccess(Long epicId) {
        if (epicId == null) { return false; }
        var epicBeingAccessed = epicService().findById(epicId);
        if (epicBeingAccessed.getProduct() == null) { return false; }
        return hasProductAccess(epicBeingAccessed.getProduct().getId());
    }

    @Override
    public Object getFilterObject() {
        return null;
    }

    @Override
    public Object getReturnObject() {
        return null;
    }

    @Override
    public void setFilterObject(Object obj) { /*Not Used*/ }

    @Override
    public void setReturnObject(Object obj) { /*Not Used*/ }

    @Override
    public Object getThis() {
        return this;
    }

}
