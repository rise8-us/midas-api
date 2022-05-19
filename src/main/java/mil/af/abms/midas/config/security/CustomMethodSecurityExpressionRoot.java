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
import mil.af.abms.midas.api.gantt.event.Event;
import mil.af.abms.midas.api.gantt.event.EventService;
import mil.af.abms.midas.api.gantt.milestone.Milestone;
import mil.af.abms.midas.api.gantt.milestone.MilestoneService;
import mil.af.abms.midas.api.gantt.target.Target;
import mil.af.abms.midas.api.gantt.target.TargetService;
import mil.af.abms.midas.api.gantt.win.Win;
import mil.af.abms.midas.api.gantt.win.WinService;
import mil.af.abms.midas.api.measure.Measure;
import mil.af.abms.midas.api.measure.MeasureService;
import mil.af.abms.midas.api.persona.PersonaService;
import mil.af.abms.midas.api.personnel.Personnel;
import mil.af.abms.midas.api.personnel.PersonnelService;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.config.SpringContext;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private static AssertionService assertionService() { return SpringContext.getBean(AssertionService.class); }
    private static CommentService commentService() { return SpringContext.getBean(CommentService.class); }
    private static EpicService epicService() { return SpringContext.getBean(EpicService.class); }
    private static EventService eventService() { return SpringContext.getBean(EventService.class); }
    private static FeatureService featureService() { return SpringContext.getBean(FeatureService.class); }
    private static FeedbackService feedbackService() { return SpringContext.getBean(FeedbackService.class); }
    private static MeasureService measureService() { return SpringContext.getBean(MeasureService.class); }
    private static MilestoneService milestoneService() { return SpringContext.getBean(MilestoneService.class); }
    private static PersonaService personaService() { return SpringContext.getBean(PersonaService.class); }
    private static PersonnelService personnelService() { return SpringContext.getBean(PersonnelService.class); }
    private static PortfolioService portfolioService() { return SpringContext.getBean(PortfolioService.class); }
    private static ProductService productService() { return SpringContext.getBean(ProductService.class); }
    private static ProjectService projectService() { return SpringContext.getBean(ProjectService.class); }
    private static TargetService targetService() { return SpringContext.getBean(TargetService.class); }
    private static UserService userService() { return SpringContext.getBean(UserService.class); }
    private static WinService winService() { return SpringContext.getBean(WinService.class); }

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

    public boolean hasPersonnelAccess(Long personnelId) {
        if (personnelId == null) { return false; }
        var personnelBeingAccessed = personnelService().findById(personnelId);
        var teamId = personnelBeingAccessed.getTeams().stream().findFirst().map(Team::getId).orElse(null);
        var userMakingRequest = userService().getUserBySecContext();
        var isPersonnelAdmin = personnelBeingAccessed.getAdmins().contains(userMakingRequest);
        var owner = personnelBeingAccessed.getOwner();
        return userMakingRequest.equals(owner) || hasTeamAccess(teamId) || isPersonnelAdmin;
    }

    public boolean hasProjectAccess(Long projectId) {
        var projectBeingAccessed = projectService().findById(projectId);
        var productContainingProject = Optional.ofNullable(projectBeingAccessed.getProduct()).orElse(new Product());
        var teamId = Optional.ofNullable(projectBeingAccessed.getTeam()).map(Team::getId).orElse(null);
        var productId = productContainingProject.getId();
        return hasTeamAccess(teamId) || hasProductAccess(productId);
    }

    public boolean hasPortfolioAccess(Long portfolioId) {
        if (portfolioId == null) { return false; }
        var portfolioBeingAccessed = portfolioService().findById(portfolioId);
        var personnelId = Optional.ofNullable(portfolioBeingAccessed.getPersonnel()).map(Personnel::getId).orElse(null);
        return hasPersonnelAccess(personnelId);
    }

    public boolean hasProductAccess(Long productId) {
        if (productId == null) { return false; }
        var productBeingAccessed = productService().findById(productId);
        var portfolio = Optional.ofNullable(productBeingAccessed.getPortfolio()).orElse(new Portfolio());
        var personnelId = Optional.ofNullable(productBeingAccessed.getPersonnel()).map(Personnel::getId).orElse(null);
        return hasPortfolioAccess(portfolio.getId()) || hasPersonnelAccess(personnelId);
    }

    public boolean hasGanttTargetModifyAccess(Long targetId) {
        if (targetId == null) { return false; }
        Target targetBeingAccessed = targetService().findById(targetId);
        Long portfolioId = Optional.ofNullable(targetBeingAccessed.getPortfolio()).map(Portfolio::getId).orElse(null);
        return hasPortfolioAccess(portfolioId);
    }

    public boolean hasGanttEventModifyAccess(Long eventId) {
        if (eventId == null) { return false; }
        Event eventBeingAccessed = eventService().findById(eventId);
        Long portfolioId = Optional.ofNullable(eventBeingAccessed.getPortfolio()).map(Portfolio::getId).orElse(null);
        return hasPortfolioAccess(portfolioId);
    }

    public boolean hasGanttMilestoneModifyAccess(Long milestoneId) {
        if (milestoneId == null) { return false; }
        Milestone milestoneBeingAccessed = milestoneService().findById(milestoneId);
        Long portfolioId = Optional.ofNullable(milestoneBeingAccessed.getPortfolio()).map(Portfolio::getId).orElse(null);
        return hasPortfolioAccess(portfolioId);
    }

    public boolean hasGanttWinModifyAccess(Long winId) {
        if (winId == null) { return false; }
        Win winBeingAccessed = winService().findById(winId);
        Long portfolioId = Optional.ofNullable(winBeingAccessed.getPortfolio()).map(Portfolio::getId).orElse(null);
        return hasPortfolioAccess(portfolioId);
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
