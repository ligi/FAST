import org.ligi.fast.settings.FASTSettings;

public class MutableFastSettings implements FASTSettings {

    public boolean launchSingle = false;
    public boolean searchPackage = false;
    public boolean convertUmlaut = false;
    public boolean ignoreSpace = false;
    public boolean gapSearch = false;

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
        return 0;
    }

}
