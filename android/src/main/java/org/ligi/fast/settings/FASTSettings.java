package org.ligi.fast.settings;

/**
 * Interface for FAST's preferences
 */
public interface FASTSettings {

    static final String KEY_LAUNCHSINGLE = "launch_single";
    static final String KEY_SEARCHPKG = "search_pkg";
    static final String KEY_MARKETFORALL = "marketforall";
    static final String KEY_TEXTONLY = "textonly";
    static final String KEY_MAXLINES = "maxlines";
    static final String KEY_ICONSIZE = "iconsize";
    static final String KEY_UMLAUTCONVERT = "convert_umlauts";
    static final String KEY_IGNORESPACEAFTERQUERY = "ignore_space";
    static final String KEY_THEME = "theme";
    static final String KEY_SORT = "sort";
    static final String KEY_SHOWKEYBOARDONSTART = "show_keyboard_on_start";
    static final String KEY_FINISH_ON_LAUNCH = "finish_on_LAUNCH";
    static final String KEY_GAP_SEARCH = "gap_search";
    static final String KEY_ICONRES = "icon_res";

    boolean isLaunchSingleActivated();

    boolean isSearchPackageActivated();

    boolean isUmlautConvertActivated();

    boolean isMarketForAllActivated();

    boolean isTextOnlyActivated();

    boolean isIgnoreSpaceAfterQueryActivated();

    boolean isShowKeyBoardOnStartActivated();

    int getMaxLines();

    int getIconResolution();

    String getIconSize();

    String getTheme();

    String getSortOrder();

    boolean isFinishOnLaunchEnabled();

    boolean isGapSearchActivated();

}
