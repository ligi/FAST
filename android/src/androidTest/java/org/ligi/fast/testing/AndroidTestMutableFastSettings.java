package org.ligi.fast.testing;

import org.ligi.fast.settings.AndroidFASTSettings;
import org.ligi.fast.settings.FASTSettings;

public class AndroidTestMutableFastSettings implements FASTSettings {

    public boolean launchSingle = false;
    public boolean searchPackage = false;
    public boolean convertUmlaut = false;
    public boolean ignoreSpace = false;
    public boolean gapSearch = false;
    public String theme = "light";

    @Override
    public boolean isLaunchSingleActivated() {
        return launchSingle;
    }

    @Override
    public boolean isSearchPackageActivated() {
        return searchPackage;
    }

    @Override
    public boolean isUmlautConvertActivated() {
        return convertUmlaut;
    }

    @Override
    public boolean isMarketForAllActivated() {
        return false;
    }

    @Override
    public boolean isTextOnlyActivated() {
        return false;
    }

    @Override
    public boolean isIgnoreSpaceAfterQueryActivated() {
        return ignoreSpace;
    }

    @Override
    public boolean isShowKeyBoardOnStartActivated() {
        return false;
    }

    @Override
    public int getMaxLines() {
        return 0;
    }

    @Override
    public String getIconSize() {
        return AndroidFASTSettings.DEFUAULT_ICONSIZE;
    }

    @Override
    public String getTheme() {
        return theme;
    }

    @Override
    public String getSortOrder() {
        return AndroidFASTSettings.DEFUAULT_SORT_ORDER;
    }


    @Override
    public boolean isFinishOnLaunchEnabled() {
        return false;
    }

    @Override
    public boolean isGapSearchActivated() {
        return gapSearch;
    }

    @Override
    public int getIconResolution() {
        return Integer.parseInt(AndroidFASTSettings.DEFUAULT_ICON_RESOLUTION);
    }

    @Override
    public boolean isShowHiddenActivated() {
        return false;
    }
}
