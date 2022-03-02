package mil.af.abms.midas.config;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.springframework.beans.BeanUtils;
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
import mil.af.abms.midas.api.comment.Comment;
import mil.af.abms.midas.api.comment.CommentService;
import mil.af.abms.midas.api.epic.Epic;
import mil.af.abms.midas.api.epic.EpicService;
import mil.af.abms.midas.api.feature.Feature;
import mil.af.abms.midas.api.feature.FeatureService;
import mil.af.abms.midas.api.feedback.Feedback;
import mil.af.abms.midas.api.feedback.FeedbackService;
import mil.af.abms.midas.api.helper.Builder;
import mil.af.abms.midas.api.measure.Measure;
import mil.af.abms.midas.api.measure.MeasureService;
import mil.af.abms.midas.api.persona.Persona;
import mil.af.abms.midas.api.persona.PersonaService;
import mil.af.abms.midas.api.product.Product;
import mil.af.abms.midas.api.product.ProductService;
import mil.af.abms.midas.api.project.Project;
import mil.af.abms.midas.api.project.ProjectService;
import mil.af.abms.midas.api.team.Team;
import mil.af.abms.midas.api.team.TeamService;
import mil.af.abms.midas.api.user.User;
import mil.af.abms.midas.api.user.UserService;
import mil.af.abms.midas.config.security.CustomMethodSecurityExpressionRoot;
import mil.af.abms.midas.helpers.TestUtil;

@ExtendWith(SpringExtension.class)
@Import({CustomMethodSecurityExpressionRoot.class, SpringContext.class})
class CustomMethodSecurityExpressionRootTests {

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
    MeasureService measureService;
    @MockBean
    ProjectService projectService;
    @MockBean
    ProductService productService;
    @MockBean
    CommentService commentService;
    @MockBean
    PersonaService personaService;
    @MockBean
    FeatureService featureService;
    @MockBean
    EpicService epicService;
    @MockBean
    FeedbackService feedbackService;

    Assertion assertion = Builder.build(Assertion.class)
            .with(a -> a.setId(7L))
            .get();
    Measure measure = Builder.build(Measure.class)
            .with(m -> m.setId(42L))
            .get();
    Team team = Builder.build(Team.class)
            .with(t -> t.setId(3L))
            .get();
    User user = Builder.build(User.class)
            .with(u -> u.setId(2L))
            .with(u -> u.setKeycloakUid("abc"))
            .with(u -> u.setTeams(Set.of(team)))
            .get();
    User user2 = Builder.build(User.class)
            .with(u -> u.setId(20L))
            .with(u -> u.setKeycloakUid("def"))
            .get();
    Project project = Builder.build(Project.class)
            .with(p -> p.setId(1L))
            .get();
    Product product = Builder.build(Product.class)
            .with(p -> p.setId(4L))
            .get();
    Product portfolio = Builder.build(Product.class)
            .with(p -> p.setId(5L))
            .get();
    Comment comment = Builder.build(Comment.class)
            .with(c -> c.setId(8L))
            .with(c -> c.setCreatedBy(user))
            .get();
    Persona persona = Builder.build(Persona.class)
            .with(p -> p.setId(9L))
            .get();
    Feature feature = Builder.build(Feature.class)
            .with(f -> f.setId(10L))
            .get();
    Epic epic = Builder.build(Epic.class)
            .with(e -> e.setId(11L))
            .get();
    Feedback feedback = Builder.build(Feedback.class)
            .with(f -> f.setId(12L))
            .with(f -> f.setCreatedBy(user))
            .get();

    @BeforeEach
    public void init() {
        project.setProduct(product);
        project.setTeam(team);
        product.setOwner(user);
        product.setParent(portfolio);
        assertion.setProduct(product);
        measure.setAssertion(assertion);
        doReturn(user).when(userService).getUserBySecContext();
    }

    @Test
    void should_control_user_access() {
        var user2 = TestUtil.clone(user);
        user2.setId(303L);
        user2.setKeycloakUid("efg");

        doReturn(user).when(userService).findById(user.getId());
        doReturn(user2).when(userService).findById(user2.getId());

        assertTrue(security.isSelf(2L));
        assertFalse(security.isSelf(303L));
    }

    @Test
    void should_hasTeamAccess() {
        assertTrue(security.hasTeamAccess(3L));
    }

    @Test
    void hasProjectAccess_should_call_with_null() {
        project.setTeam(null);
        project.setProduct(null);
        when(projectService.findById(1L)).thenReturn(project);

        assertFalse(security.hasProjectAccess(1L));
    }

    @Test
    void hasProjectAccess_should_call_with_team_and_product() {
        when(projectService.findById(1L)).thenReturn(project);
        doReturn(true).when(security).hasTeamAccess(3L);
        doReturn(true).when(security).hasProductAccess(4L);

        assertTrue(security.hasProjectAccess(1L));
    }

    @Test
    void hasProjectAccess_should_call_with_team_false_and_product_false() {
        when(projectService.findById(1L)).thenReturn(project);
        doReturn(false).when(security).hasTeamAccess(3L);
        doReturn(false).when(security).hasProductAccess(4L);

        assertFalse(security.hasProjectAccess(1L));
    }

    @Test
    void hasProjectAccess_should_call_with_team_true_and_product_false() {
        when(projectService.findById(1L)).thenReturn(project);
        doReturn(true).when(security).hasTeamAccess(3L);
        doReturn(false).when(security).hasProductAccess(4L);

        assertTrue(security.hasProjectAccess(1L));
    }

    @Test
    void hasProjectAccess_should_call_with_team_false_and_product_true() {
        when(projectService.findById(1L)).thenReturn(project);
        doReturn(false).when(security).hasTeamAccess(3L);
        doReturn(true).when(security).hasProductAccess(4L);

        assertTrue(security.hasProjectAccess(1L));
    }

    @Test
    void hasProductAccess_should_call_with_productManager_true_and_product_false() {
        when(productService.findById(product.getId())).thenReturn(product);
        doReturn(false).when(security).hasProductAccess(5L);

        assertTrue(security.hasProductAccess(4L));
    }

    @Test
    void hasProductAccess_should_call_with_productManager_false_and_product_true() {
        product.setOwner(null);
        when(productService.findById(product.getId())).thenReturn(product);
        doReturn(true).when(security).hasProductAccess(5L);

        assertTrue(security.hasProductAccess(4L));
    }

    @Test
    void hasProductAccess_should_call_with_productManager_false_and_product_false() {
        product.setOwner(null);
        when(productService.findById(product.getId())).thenReturn(product);
        doReturn(false).when(security).hasProductAccess(5L);

        assertFalse(security.hasProductAccess(4L));
    }

    @Test
    void hasProductAccess_should_create_new_product_for_parent() {
        product.setParent(null);
        when(productService.findById(product.getId())).thenReturn(product);
        doReturn(false).when(security).hasProductAccess(null);

        assertTrue(security.hasProductAccess(4L));
    }

    @Test
    void hasAssertionWriteAccess_assertionId_null() {
        assertFalse(security.hasAssertionWriteAccess(null));
    }

    @Test
    void hasAssertionWriteAccess_true() {
        when(assertionService.findById(assertion.getId())).thenReturn(assertion);
        doReturn(true).when(security).hasProductAccess(product.getId());

        assertTrue(security.hasAssertionWriteAccess(assertion.getId()));
    }

    @Test
    void hasAssertionWriteAccess_false_when_productId_null() {
        assertion.setProduct(null);
        when(assertionService.findById(assertion.getId())).thenReturn(assertion);

        assertFalse(security.hasAssertionWriteAccess(assertion.getId()));
    }

    @Test
    void hasMeasureWriteAccess_measureId_null() {
        assertFalse(security.hasMeasureWriteAccess(null));
    }

    @Test
    void hasMeasureWriteAccess_true() {
        when(measureService.findById(measure.getId())).thenReturn(measure);
        doReturn(true).when(security).hasAssertionWriteAccess(assertion.getId());
        doReturn(true).when(security).hasProductAccess(product.getId());

        assertTrue(security.hasMeasureWriteAccess(measure.getId()));
    }

    @Test
    void hasMeasureWriteAccess_false_when_assertionId_null() {
        measure.setAssertion(null);
        when(measureService.findById(measure.getId())).thenReturn(measure);

        assertFalse(security.hasMeasureWriteAccess(measure.getId()));
    }
    
    @Test
    void should_getThis() {
        assertThat(security.getThis()).isEqualTo(security);
    }

    @Test
    void should_return_null_on_getFilterObject() {
        assertNull(security.getFilterObject());
    }

    @Test
    void should_return_null_on_getReturnObject() {
        assertNull(security.getReturnObject());
    }

    @Test
    void should_increase_test_coverage_for_P1_CtF_otherwise_pointless_setFilterObject() {
        security.setFilterObject(new Object());
        assertNull(security.getFilterObject());
    }

    @Test
    void should_increase_test_coverage__for_P1_CtF__otherwise_pointless_setReturnObject() {
        security.setReturnObject(new Object());
        assertNull(security.getReturnObject());
    }

    @Test
    void isCommentCreator_false_when_commentId_null() {
        assertFalse(security.isCommentCreator(null));
    }

    @Test
    void isCommentCreator_should_return_false() {
        doReturn(user2).when(userService).getUserBySecContext();
        when(commentService.findById(anyLong())).thenReturn(comment);

        assertFalse(security.isCommentCreator(8L));
    }

    @Test
    void isCommentCreator_should_return_true() {
        when(commentService.findById(anyLong())).thenReturn(comment);

        assertTrue(security.isCommentCreator(8L));
    }

    @Test
    void isFeedbackCreator_false_when_commentId_null() {
        assertFalse(security.isFeedbackCreator(null));
    }

    @Test
    void isFeedbackCreator_should_return_false() {
        doReturn(user2).when(userService).getUserBySecContext();
        when(feedbackService.findById(anyLong())).thenReturn(feedback);

        assertFalse(security.isFeedbackCreator(12L));
    }

    @Test
    void isFeedbackCreator_should_return_true() {
        when(feedbackService.findById(anyLong())).thenReturn(feedback);

        assertTrue(security.isFeedbackCreator(12L));
    }

    @Test
    void hasPersonaAccess_false_when_personaId_null_or_no_product() {
        when(personaService.findById(anyLong())).thenReturn(persona);

        assertFalse(security.hasPersonaUpdateAccess(null));
        assertFalse(security.hasPersonaUpdateAccess(9L));
    }

    @Test
    void hasPersonaAccess_true_when_hasProductAccess() {
        var persona2 = new Persona();
        BeanUtils.copyProperties(persona, persona2);
        persona2.setProduct(product);

        when(personaService.findById(anyLong())).thenReturn(persona2);
        doReturn(true).when(security).hasProductAccess(anyLong());

        assertTrue(security.hasPersonaUpdateAccess(9L));
    }

    @Test
    void hasFeatureAccess_false_when_featureId_null_or_no_product() {
        when(featureService.findById(anyLong())).thenReturn(feature);

        assertFalse(security.hasFeatureUpdateAccess(null));
        assertFalse(security.hasFeatureUpdateAccess(10L));
    }

    @Test
    void hasFeatureAccess_true_when_hasProductAccess() {
        var feature2 = new Feature();
        BeanUtils.copyProperties(feature, feature2);
        feature2.setProduct(product);

        when(featureService.findById(anyLong())).thenReturn(feature2);
        doReturn(true).when(security).hasProductAccess(anyLong());

        assertTrue(security.hasFeatureUpdateAccess(10L));
    }

    @Test
    void hasEpicHideAccess_false_when_epicId_null_or_no_product() {
        when(epicService.findById(anyLong())).thenReturn(epic);

        assertFalse(security.hasEpicHideAccess(null));
        assertFalse(security.hasEpicHideAccess(11L));
    }

    @Test
    void hasEpicHideAccess_true_when_hasProductAccess() {
        var epic2 = new Epic();
        BeanUtils.copyProperties(epic, epic2);
        epic2.setProduct(product);

        when(epicService.findById(anyLong())).thenReturn(epic2);
        doReturn(true).when(security).hasProductAccess(anyLong());

        assertTrue(security.hasEpicHideAccess(11L));
    }

}
