package mil.af.abms.midas.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Classification {

    UNCLASS("UNCLASSIFIED", Constants.GREEN, Constants.WHITE),
    CUI("CUI", Constants.PURPLE, Constants.WHITE),
    SECRET("SECRET", Constants.RED, Constants.WHITE),
    SCI("SCI", Constants.YELLOW, Constants.BLACK);

    private final String name;
    private final String backgroundColor;
    private final String textColor;

    private static class Constants {
        public static final String WHITE = "#FFFFFF";
        public static final String BLACK = "#000000";
        public static final String GREEN = "#5bad76";
        public static final String PURPLE = "#2849b8";
        public static final String RED = "#be4242";
        public static final String YELLOW = "#eff01a";
    }
}
