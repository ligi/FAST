package org.ligi.fast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class to handle the Preferences
 */
public class AndroidFASTSettings implements FASTSettings {


    private final SharedPreferences mSharedPreferences;

    public AndroidFASTSettings(Context ctx) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public boolean isLaunchSingleActivated() {
        return mSharedPreferences.getBoolean(KEY_LAUNCHSINGLE, false);
    }

    public boolean isSearchPackageActivated() {
        return mSharedPreferences.getBoolean(KEY_SEARCHPKG, false);
    }

    public boolean isUmlautConvertActivated() {
        return mSharedPreferences.getBoolean(KEY_UMLAUTCONVERT, false);
    }

    public boolean isMarketForAllActivated() {
        return mSharedPreferences.getBoolean(KEY_MARKETFORALL, false);
    }

    public boolean isTextOnlyActive() {
        return mSharedPreferences.getBoolean(KEY_TEXTONLY, false);
    }

    public int getMaxLines() {
        return Integer.parseInt(mSharedPreferences.getString(KEY_MAXLINES, "1"));
    }

    public String getIconSize() {
        return mSharedPreferences.getString(KEY_ICONSIZE, "medium");
    }

    public String getTheme() {
        return mSharedPreferences.getString(KEY_THEME, "dark");
    }

    public String getSortOrder() {
        return mSharedPreferences.getString(KEY_SORT, "unsorted");
    }


}
