package mil.af.abms.midas.config.security;

public final class AuthExpression {

    private static final String HAS_AUTHORITY = "hasAuthority('";
    public static final String ADMIN = "ADMIN";
    public static final String PORTFOLIO_LEAD = "PORTFOLIO_LEAD";
    public static final String PRODUCT_MANAGER = "PRODUCT_MANAGER";
    public static final String OR = " or ";
    public static final String END_EXP =  "')";
    public static final String IS_ADMIN = HAS_AUTHORITY + ADMIN + END_EXP;
    public static final String IS_PORTFOLIO_LEAD = HAS_AUTHORITY + PORTFOLIO_LEAD + END_EXP;
    public static final String IS_PRODUCT_MANAGER = HAS_AUTHORITY + PRODUCT_MANAGER + END_EXP;
    public static final String HAS_PROJECT_ACCESS = "hasProjectAccess(#id)" + OR + IS_ADMIN;
    public static final String HAS_PRODUCT_ACCESS = "hasProductAccess(#id)" + OR + IS_ADMIN;
    public static final String HAS_OGSM_UPDATE_ACCESS = "hasOGSMWriteAccess(#id)" + OR + IS_ADMIN;
    public static final String HAS_OGSM_CREATE_ACCESS = "hasProductAccess(#createAssertionDTO.getProductId())"+ OR + IS_ADMIN;
    public static final String HAS_PROJECT_CREATE_ACCESS = HAS_PROJECT_ACCESS + OR + PORTFOLIO_LEAD + OR +PRODUCT_MANAGER;

    private AuthExpression() {
        throw new IllegalStateException("Utility Class");
    }
}

