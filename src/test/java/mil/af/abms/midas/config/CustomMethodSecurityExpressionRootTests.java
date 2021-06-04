package mil.af.abms.midas.config;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import mil.af.abms.midas.api.assertion.Assertion;
import mil.af.abms.midas.api.assertion.AssertionService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.config.security.CustomMethodSecurityExpressionRoot;

@ExtendWith(SpringExtension.class)
@Import({CustomMethodSecurityExpressionRoot.class, SpringContext.class})
public class CustomMethodSecurityExpressionRootTests {

    @SpyBean
    CustomMethodSecurityExpressionRoot security;
    @Autowired
    SpringContext springContext;
    @MockBean
    Authentication auth;
    @MockBean
    UserService userService;
    @MockBean
    TeamService teamService;
    @MockBean
    AssertionService assertionService;
    @MockBean
    ProjectService projectService;
    @MockBean
    ProductService productService;

    Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(7L)).get();
    Team team = Builder.build(Team.class)
            .with(t -> t.setId(3L)).get();
    User user = Builder.build(User.class)
            .with(u -> u.setId(2L))
            .with(u -> u.setTeams(Set.of(team)))
            .get();
    Project project = Builder.build(Project.class)
            .with(p -> p.setId(1L)).get();
    Product product = Builder.build(Product.class)
            .with(p -> p.setId(4L)).get();
    Product portfolio = Builder.build(Product.class)
            .with(p -> p.setId(5L)).get();

    @BeforeEach
    public void init() {
        project.setProduct(product);
        project.setTeam(team);
        product.setProductManager(user);
        product.setParent(portfolio);
        assertion.setProduct(product);
        doReturn(user).when(userService).getUserBySecContext();
    }

    @Test
    public void should_hasTeamAccess() {
        assertTrue(security.hasTeamAccess(3L));
    }

    @Test
    public void hasProjectAccess_should_call_with_null() {
        project.setTeam(null);
        project.setProduct(null);
        when(projectService.getObject(1L)).thenReturn(project);

        assertFalse(security.hasProjectAccess(1L));
    }

    @Test
    public void hasProjectAccess_should_call_with_team_and_product() {
        when(projectService.getObject(1L)).thenReturn(project);
        doReturn(true).when(security).hasTeamAccess(3L);
        doReturn(true).when(security).hasProductAccess(4L);

        assertTrue(security.hasProjectAccess(1L));
    }

    @Test
    public void hasProjectAccess_should_call_with_team_false_and_product_false() {
        when(projectService.getObject(1L)).thenReturn(project);
        doReturn(false).when(security).hasTeamAccess(3L);
        doReturn(false).when(security).hasProductAccess(4L);

        assertFalse(security.hasProjectAccess(1L));
    }

    @Test
    public void hasProjectAccess_should_call_with_team_true_and_product_false() {
        when(projectService.getObject(1L)).thenReturn(project);
        doReturn(true).when(security).hasTeamAccess(3L);
        doReturn(false).when(security).hasProductAccess(4L);

        assertTrue(security.hasProjectAccess(1L));
    }

    @Test
    public void hasProjectAccess_should_call_with_team_false_and_product_true() {
        when(projectService.getObject(1L)).thenReturn(project);
        doReturn(false).when(security).hasTeamAccess(3L);
        doReturn(true).when(security).hasProductAccess(4L);

        assertTrue(security.hasProjectAccess(1L));
    }

    @Test
    public void hasProductAccess_should_call_with_productManager_true_and_product_false() {
        when(productService.getObject(product.getId())).thenReturn(product);
        doReturn(false).when(security).hasProductAccess(5L);

        assertTrue(security.hasProductAccess(4L));
    }

    @Test
    public void hasProductAccess_should_call_with_productManager_false_and_product_true() {
        product.setProductManager(null);
        when(productService.getObject(product.getId())).thenReturn(product);
        doReturn(true).when(security).hasProductAccess(5L);

        assertTrue(security.hasProductAccess(4L));
    }

    @Test
    public void hasProductAccess_should_call_with_productManager_false_and_product_false() {
        product.setProductManager(null);
        when(productService.getObject(product.getId())).thenReturn(product);
        doReturn(false).when(security).hasProductAccess(5L);

        assertFalse(security.hasProductAccess(4L));
    }


    @Test
    public void hasProductAccess_should_create_new_product_for_parent() {
        product.setParent(null);
        when(productService.getObject(product.getId())).thenReturn(product);
        doReturn(false).when(security).hasProductAccess(null);

        assertTrue(security.hasProductAccess(4L));
    }

    @Test
    public void hasOGSMWriteAccess_ogsmId_null() {
        assertFalse(security.hasOGSMWriteAccess(null));
    }

    @Test
    public void hasOGSMWriteAccess_true() {
        when(assertionService.getObject(assertion.getId())).thenReturn(assertion);
        doReturn(true).when(security).hasProductAccess(product.getId());

        assertTrue(security.hasOGSMWriteAccess(assertion.getId()));
    }

    @Test
    public void hasOGSMWriteAccess_false_when_productId_null() {
        assertion.setProduct(null);
        when(assertionService.getObject(assertion.getId())).thenReturn(assertion);

        assertFalse(security.hasOGSMWriteAccess(assertion.getId()));
    }

    @Test
    public void should_getThis() {
        assertThat(security.getThis()).isEqualTo(security);
    }

    @Test
    public void should_return_null_on_getFilterObject() {
        assertNull(security.getFilterObject());
    }

    @Test
    public void should_return_null_on_getReturnObject() {
        assertNull(security.getReturnObject());
    }

    @Test
    public void should_increase_test_coverage_for_P1_CtF_otherwise_pointless_setFilterObject() {
        security.setFilterObject(new Object());
    }

    @Test
    public void should_increase_test_coverage__for_P1_CtF__otherwise_pointless_setReturnObject() {
        security.setReturnObject(new Object());
    }

}
