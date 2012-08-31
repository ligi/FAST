package org.ligi.fast;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FASTPrefs {

	private SharedPreferences mSharedPreferences;
	
	public final static String KEY_LAUNCHSINGLE="launch_single";
	
	public FASTPrefs(Context ctx) {
		mSharedPreferences=PreferenceManager.getDefaultSharedPreferences(ctx);
	}
	
	public boolean isLaunchSingleActivated() {
		return mSharedPreferences.getBoolean(KEY_LAUNCHSINGLE,false);
	}
}
