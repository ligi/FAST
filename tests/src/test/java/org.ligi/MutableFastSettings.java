package org.ligi;

import org.ligi.fast.FASTSettings;

public class MutableFastSettings implements FASTSettings{

    public boolean launchSingle=false;
    public boolean searchPackage=false;
    public boolean convertUmlaut=false;

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
        return false;
    }

    @Override
    public boolean isMarketForAllActivated() {
        return false;
    }

    @Override
    public boolean isTextOnlyActive() {
        return false;
    }

    @Override
    public int getMaxLines() {
        return 0;
    }

    @Override
    public String getIconSize() {
        return null;
    }

    @Override
    public String getTheme() {
        return null;
    }

    @Override
    public String getSortOrder() {
        return null;
    }
}
