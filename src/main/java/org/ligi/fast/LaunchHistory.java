package org.ligi.fast;

import android.content.Context;
import android.content.SharedPreferences;

public class LaunchHistory {

    private static final String PREFERENCES_LAUNCH_HISTORY = "launch_history";
    private SharedPreferences launchHistory;

    private static LaunchHistory instance = null;

    public static LaunchHistory getInstance(Context context) {
        if (instance == null) {
            instance = new LaunchHistory(context);
        }
        return instance;
    }

    protected LaunchHistory(Context context) {
        launchHistory = context.getSharedPreferences(PREFERENCES_LAUNCH_HISTORY, Context.MODE_PRIVATE);
    }

    private String getKey(AppInfo appInfo) {
        return appInfo.getActivityName();
    }

    public int getCount(AppInfo appInfo) {
        return launchHistory.getInt(getKey(appInfo), 0);
    }

    public void launch(AppInfo appInfo) {
        launchHistory.edit().putInt(getKey(appInfo), getCount(appInfo) + 1).commit();
    }

}
