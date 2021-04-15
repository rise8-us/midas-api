package mil.af.abms.midas.config.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import mil.af.abms.midas.api.application.Application;
import mil.af.abms.midas.api.application.ApplicationService;
import mil.af.abms.midas.api.portfolio.Portfolio;
import mil.af.abms.midas.api.portfolio.PortfolioService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.config.SpringContext;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private static ApplicationService applicationService() {
        return SpringContext.getBean(ApplicationService.class);
    }
    private static UserService userService() {
        return SpringContext.getBean(UserService.class);
    }
    private static ProjectService projectService() {
        return SpringContext.getBean(ProjectService.class);
    }
    private static PortfolioService portfolioService() {
        return SpringContext.getBean(PortfolioService.class);
    }

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean hasTeamAccess(Long teamToModifyId) {
        User userMakingRequest = userService().getUserBySecContext();
        return userMakingRequest.getTeamIds().contains(teamToModifyId);
    }

    public boolean hasProjectAccess(Long projectId) {
        Project projectBeingAccesed = projectService().getObject(projectId);
        Application applicationContainingProject = projectBeingAccesed.getApplication() != null ?
                projectBeingAccesed.getApplication() : new Application();
        Long teamId  = projectBeingAccesed.getTeam() != null ? projectBeingAccesed.getTeam().getId() : null;
        Long applicationId = applicationContainingProject.getId();
        Long portfolioId  = applicationContainingProject.getPortfolio() != null ?
                applicationContainingProject.getPortfolio().getId() : null;
        return hasTeamAccess(teamId) || hasApplicationAccess(applicationId) || hasPortfolioAccess(portfolioId);
    }

    public boolean hasApplicationAccess(Long applicationId) {
        if (applicationId == null) { return false; }
        Application applicationBeingAccessed = applicationService().getObject(applicationId);
        Portfolio portfolio = applicationBeingAccessed.getPortfolio() != null ?
                applicationBeingAccessed.getPortfolio() : new Portfolio();
        User userMakingRequest = userService().getUserBySecContext();
        User productManager = applicationService().getObject(applicationId).getProductManager();
        return userMakingRequest.equals(productManager) || hasPortfolioAccess(portfolio.getId());
    }

    public boolean hasPortfolioAccess(Long portfolioId) {
        if (portfolioId == null) { return false; }
        User userMakingRequest = userService().getUserBySecContext();
        User portfolioManager =  portfolioService().getObject(portfolioId).getPortfolioManager();
        return userMakingRequest.equals(portfolioManager);
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
    public void setFilterObject(Object obj) { }

    @Override
    public void setReturnObject(Object obj) { }

    @Override
    public Object getThis() {
        return this;
    }

}
