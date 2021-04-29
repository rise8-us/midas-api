package mil.af.abms.midas.config.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.config.SpringContext;

public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private static ProductService productService() {
        return SpringContext.getBean(ProductService.class);
    }
    private static UserService userService() {
        return SpringContext.getBean(UserService.class);
    }
    private static ProjectService projectService() {
        return SpringContext.getBean(ProjectService.class);
    }

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean hasTeamAccess(Long teamToModifyId) {
        User userMakingRequest = userService().getUserBySecContext();
        return userMakingRequest.getTeamIds().contains(teamToModifyId);
    }

    public boolean hasProjectAccess(Long projectId) {
        Project projectBeingAccessed = projectService().getObject(projectId);
        Product productContainingProject = projectBeingAccessed.getProduct() != null ?
                projectBeingAccessed.getProduct() : new Product();
        Long teamId  = projectBeingAccessed.getTeam() != null ? projectBeingAccessed.getTeam().getId() : null;
        Long productId = productContainingProject.getId();
        Long parentId  = productContainingProject.getParent() != null ?
                productContainingProject.getParent().getId() : null;
        return hasTeamAccess(teamId) || hasProductAccess(productId);
    }

    public boolean hasProductAccess(Long productId) {
        if (productId == null) { return false; }
        Product productBeingAccessed = productService().getObject(productId);
        Product parent = productBeingAccessed.getParent() != null ?
                productBeingAccessed.getParent() : new Product();
        User userMakingRequest = userService().getUserBySecContext();
        User productManager = productService().getObject(productId).getProductManager();
        return userMakingRequest.equals(productManager) || hasProductAccess(parent.getId());
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
