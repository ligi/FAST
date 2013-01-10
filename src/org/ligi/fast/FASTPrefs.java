package org.ligi.fast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
/**
 * Class to handle the Preferences 
 *
 * @author Marcus -ligi- Büschleb
 * 
 * License GPLv3
 *
 */
public class FASTPrefs {

	private SharedPreferences mSharedPreferences;
	
	public final static String KEY_LAUNCHSINGLE="launch_single";
	public static final String KEY_SEARCHPKG = "search_pkg";
	public static final String KEY_MARKETFORALL = "marketforall";
	public static final String KEY_TEXTONLY ="textonly";
    public static final String KEY_MAXLINES = "maxlines";
    public static final String KEY_ICONSIZE = "iconsize";
	
	public FASTPrefs(Context ctx) {
		mSharedPreferences=PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public boolean isLaunchSingleActivated() {
		return mSharedPreferences.getBoolean(KEY_LAUNCHSINGLE,false);
	}
	
	public boolean isSearchPackageActivated() {
		return mSharedPreferences.getBoolean(KEY_SEARCHPKG,false);
	}
	
	public boolean isMarketForAllActivated() {
		return mSharedPreferences.getBoolean(KEY_MARKETFORALL,false);
	}
	
	public boolean isTextOnlyActive() {
		return mSharedPreferences.getBoolean(KEY_TEXTONLY,false);
	}
	
	public int getMaxLines() {
		return Integer.parseInt(mSharedPreferences.getString(KEY_MAXLINES, "1"));
	}

    public String getIconSize() {
        return mSharedPreferences.getString(KEY_ICONSIZE, "medium");
    }
}
