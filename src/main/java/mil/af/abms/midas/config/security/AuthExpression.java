package mil.af.abms.midas.config.security;

public final class AuthExpression {

    private static final String HAS_AUTHORITY = "hasAuthority('";
    public static final String END_EXP =  "')";
    public static final String OR = " or ";

    // Role Enum names
    public static final String ADMIN = "ADMIN";
    public static final String PORTFOLIO_LEAD = "PORTFOLIO_LEAD";
    public static final String PRODUCT_MANAGER = "PRODUCT_MANAGER";

    // Roles Enum
    public static final String IS_ADMIN = HAS_AUTHORITY + ADMIN + END_EXP;
    public static final String IS_PORTFOLIO_LEAD = HAS_AUTHORITY + PORTFOLIO_LEAD + END_EXP;
    public static final String IS_PRODUCT_MANAGER = HAS_AUTHORITY + PRODUCT_MANAGER + END_EXP;

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

    // Assertion
    public static final String HAS_OGSM_UPDATE_ACCESS = "hasOGSMWriteAccess(#id)" + OR + IS_ADMIN;
    public static final String HAS_OGSM_CREATE_ACCESS = "hasProductAccess(#createAssertionDTO.getProductId())" + OR + IS_ADMIN;

    // Persona
    public static final String HAS_PERSONA_ACCESS = "hasPersonaAccess(#id)" + OR + IS_ADMIN;

    // Feature
    public static final String HAS_FEATURE_ACCESS = "hasFeatureAccess(#id)" + OR + IS_ADMIN;

    private AuthExpression() {
        throw new IllegalStateException("Utility Class");
    }
}

