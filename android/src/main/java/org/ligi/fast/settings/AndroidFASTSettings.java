package org.ligi.fast.settings;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

/**
 * Class to handle the Preferences
 */
public class AndroidFASTSettings implements FASTSettings {

    public static final String DEFUAULT_SORT_ORDER = "unsorted";
    public static final String DEFUAULT_THEME = "dark";
    public static final String DEFUAULT_ICONSIZE = "medium";
    public static final String DEFUAULT_ICON_RESOLUTION = "96";
    private final SharedPreferences mSharedPreferences;
    private final ContentResolver mContentResolver;

    public AndroidFASTSettings(Context ctx) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        mContentResolver = ctx.getContentResolver();
    }

    public boolean isLaunchSingleActivated() {
        return mSharedPreferences.getBoolean(KEY_LAUNCHSINGLE, true);
    }

    public boolean isSearchPackageActivated() {
        return mSharedPreferences.getBoolean(KEY_SEARCHPKG, true);
    }

    public boolean isUmlautConvertActivated() {
        return mSharedPreferences.getBoolean(KEY_UMLAUTCONVERT, true);
    }

    public boolean isMarketForAllActivated() {
        return mSharedPreferences.getBoolean(KEY_MARKETFORALL, false);
    }

    public boolean isIgnoreSpaceAfterQueryActivated() {
        return mSharedPreferences.getBoolean(KEY_IGNORESPACEAFTERQUERY, true);
    }


    public boolean isFinishOnLaunchEnabled() {
        return mSharedPreferences.getBoolean(KEY_FINISH_ON_LAUNCH, true);
    }

    @Override
    public boolean isShowKeyBoardOnStartActivated() {
        return mSharedPreferences.getBoolean(KEY_SHOWKEYBOARDONSTART, true);
    }

    public boolean isTextOnlyActivated() {
        return mSharedPreferences.getBoolean(KEY_TEXTONLY, false);
    }

    public int getMaxLines() {
        return Integer.parseInt(mSharedPreferences.getString(KEY_MAXLINES, "1"));
    }

    @Override
    public int getIconResolution() {
        return Integer.parseInt(mSharedPreferences.getString(KEY_ICONRES, DEFUAULT_ICON_RESOLUTION));
    }

    public String getIconSize() {
        return mSharedPreferences.getString(KEY_ICONSIZE, DEFUAULT_ICONSIZE);
    }

    public String getTheme() {
        return mSharedPreferences.getString(KEY_THEME, DEFUAULT_THEME);
    }

    public String getSortOrder() {
        return mSharedPreferences.getString(KEY_SORT, DEFUAULT_SORT_ORDER);
    }

    public boolean isGapSearchActivated() {
        return mSharedPreferences.getBoolean(KEY_GAP_SEARCH, true);
    }

    public boolean isShowHiddenActivated() {
        return mSharedPreferences.getBoolean(KEY_SHOW_HIDDEN, false);
    }

    /**
     * Checks if the system was rebooted since the last call and if yes, resets the known sequence number before returning it
     *
     * @return the currently known pm sequence number or default
     */
    @TargetApi(26)
    public int getSequenceNumber() {
        int currentBootCount =
                Settings.Global.getInt(mContentResolver, Settings.Global.BOOT_COUNT, 0);
        int knownBootCount = mSharedPreferences.getInt(KEY_KNOWN_BOOT_COUNT, 0);
        if (currentBootCount > knownBootCount) {
            SharedPreferences.Editor mEditor = mSharedPreferences.edit();
            mEditor.putInt(KEY_KNOWN_PM_SEQUENCE_NUMBER, DEFAULT_KNOWN_PM_SEQUENCE_NUMBER);
            mEditor.putInt(KEY_KNOWN_BOOT_COUNT, currentBootCount);
            mEditor.apply();
            return DEFAULT_KNOWN_PM_SEQUENCE_NUMBER;
        }
        return mSharedPreferences.getInt(KEY_KNOWN_PM_SEQUENCE_NUMBER, DEFAULT_KNOWN_PM_SEQUENCE_NUMBER);
    }

    public void putSequenceNumber(int sequenceNumber) {
        SharedPreferences.Editor mEditor = mSharedPreferences.edit();
        mEditor.putInt(KEY_KNOWN_PM_SEQUENCE_NUMBER, sequenceNumber);
        mEditor.apply();
    }
}
