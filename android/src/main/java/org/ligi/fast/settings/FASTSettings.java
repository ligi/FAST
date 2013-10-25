package org.ligi.fast.settings;

/**
 * Interface for FAST's preferences
 */
public interface FASTSettings {

    public final static String KEY_LAUNCHSINGLE = "launch_single";
    public static final String KEY_SEARCHPKG = "search_pkg";
    public static final String KEY_MARKETFORALL = "marketforall";
    public static final String KEY_TEXTONLY = "textonly";
    public static final String KEY_MAXLINES = "maxlines";
    public static final String KEY_ICONSIZE = "iconsize";
    public static final String KEY_UMLAUTCONVERT = "convert_umlauts";
    public static final String KEY_IGNORESPACEAFTERQUERY = "ignore_space";
    public static final String KEY_THEME = "theme";
    public static final String KEY_SORT = "sort";
    public static final String KEY_SHOWKEYBOARDONSTART = "show_keyboard_on_start";

    public boolean isLaunchSingleActivated();

    public boolean isSearchPackageActivated();

    public boolean isUmlautConvertActivated();

    public boolean isMarketForAllActivated();

    public boolean isTextOnlyActivated();

    public boolean isIgnoreSpaceAfterQueryActivated();

    public boolean isShowKeyBoardOnStartActivated();

    public int getMaxLines();

    public String getIconSize();

    public String getTheme();

    public String getSortOrder();

}
