package mil.af.abms.midas.config.security;

public final class AuthExpression {

    private static final String HAS_AUTHORITY = "hasAuthority('";
    public static final String END_EXP =  "')";
    public static final String OR = " or ";

    // Role Enum names
    public static final String ADMIN = "ADMIN";
    public static final String PORTFOLIO_LEAD = "PORTFOLIO_LEAD";
    public static final String PRODUCT_MANAGER = "PRODUCT_MANAGER";
    public static final String PLATFORM_OPERATOR = "PLATFORM_OPERATOR";
    public static final String PORTFOLIO_ADMIN = "PORTFOLIO_ADMIN";

    // Roles Enum
    public static final String IS_ADMIN = HAS_AUTHORITY + ADMIN + END_EXP;
    public static final String IS_PORTFOLIO_LEAD = HAS_AUTHORITY + PORTFOLIO_LEAD + END_EXP;
    public static final String IS_PRODUCT_MANAGER = HAS_AUTHORITY + PRODUCT_MANAGER + END_EXP;
    public static final String IS_PLATFORM_OPERATOR = HAS_AUTHORITY + PLATFORM_OPERATOR + END_EXP;
    public static final String IS_PORTFOLIO_ADMIN = HAS_AUTHORITY + PORTFOLIO_ADMIN + END_EXP;

    // Roles and Admin
    public static final String IS_PLATFORM_OPERATOR_OR_ADMIN = IS_PLATFORM_OPERATOR + OR + IS_ADMIN;
    public static final String IS_PORTFOLIO_ADMIN_OR_ADMIN = IS_PORTFOLIO_ADMIN + OR + IS_ADMIN;
    public static final String IS_PORTFOLIO_LEADERSHIP_OR_ADMIN = IS_PORTFOLIO_ADMIN + OR + IS_PORTFOLIO_LEAD + OR + IS_ADMIN;

    // User
    public static final String SELF = "isSelf(#id)";
    public static final String HAS_USER_UPDATE_ACCESS = SELF + OR + IS_ADMIN;

    // Comment
    public static final String HAS_COMMENT_DELETE = "isCommentCreator(#id)" + OR + IS_ADMIN;

    // Project
    public static final String HAS_PROJECT_ACCESS = "hasProjectAccess(#id)" + OR + IS_ADMIN;

    // Product
    public static final String HAS_PRODUCT_ACCESS = "hasProductAccess(#id)" + OR + IS_ADMIN;
    public static final String HAS_PRODUCT_CREATE_ACCESS = IS_PORTFOLIO_LEAD + OR + IS_PRODUCT_MANAGER + OR + IS_ADMIN;
    public static final String HAS_PORTFOLIO_ACCESS = "hasPortfolioAccess(#id)" + OR + IS_ADMIN;

    // Assertion
    public static final String HAS_ASSERTION_CREATE_ACCESS = "hasProductAccess(#createAssertionDTO.getProductId())" + OR + IS_ADMIN;
    public static final String HAS_ASSERTION_UPDATE_ACCESS = "hasAssertionWriteAccess(#id)" + OR + IS_ADMIN;

    // Target
    public static final String HAS_TARGET_CREATE_ACCESS = "hasPortfolioAccess(#createTargetDTO.getPortfolioId())" + OR + IS_ADMIN;
    public static final String HAS_TARGET_MODIFY_ACCESS = "hasGanttTargetModifyAccess(#id)" + OR + IS_ADMIN;

    // Milestone
    public static final String HAS_MILESTONE_CREATE_ACCESS = "hasPortfolioAccess(#createMilestoneDTO.getPortfolioId())" + OR + IS_ADMIN;
    public static final String HAS_MILESTONE_MODIFY_ACCESS = "hasGanttMilestoneModifyAccess(#id)" + OR + IS_ADMIN;

    // Event
    public static final String HAS_EVENT_CREATE_ACCESS = "hasPortfolioAccess(#createEventDTO.getPortfolioId())" + OR + IS_ADMIN;
    public static final String HAS_EVENT_MODIFY_ACCESS = "hasGanttEventModifyAccess(#id)" + OR + IS_ADMIN;

    //Win
    public static final String HAS_WIN_CREATE_ACCESS = "hasPortfolioAccess(#createWinDTO.getPortfolioId())" + OR + IS_ADMIN;
    public static final String HAS_WIN_MODIFY_ACCESS = "hasGanttWinModifyAccess(#id)" + OR + IS_ADMIN;

    // Measure
    public static final String HAS_MEASURE_CREATE_ACCESS = "hasAssertionWriteAccess(#createMeasureDTO.getAssertionId())" + OR + IS_ADMIN;
    public static final String HAS_MEASURE_UPDATE_ACCESS = "hasMeasureWriteAccess(#id)" + OR + IS_ADMIN;

    // Feedback
    public static final String HAS_FEEDBACK_EDIT_ACCESS = "isFeedbackCreator(#id)" + OR + IS_ADMIN;

    // Persona
    public static final String HAS_PERSONA_CREATE_ACCESS = "hasProductAccess(#createPersonaDTO.getProductId())" + OR + IS_ADMIN;
    public static final String HAS_PERSONA_UPDATE_ACCESS = "hasPersonaUpdateAccess(#id)" + OR + IS_ADMIN;

    // Feature
    public static final String HAS_FEATURE_CREATE_ACCESS = "hasProductAccess(#createFeatureDTO.getProductId())" + OR + IS_ADMIN;
    public static final String HAS_FEATURE_UPDATE_ACCESS = "hasFeatureUpdateAccess(#id)" + OR + IS_ADMIN;

    // Epic
    public static final String HAS_EPIC_HIDE_ACCESS = "hasEpicHideAccess(#id)" + OR + IS_ADMIN;

    private AuthExpression() {
        throw new IllegalStateException("Utility Class");
    }
}

