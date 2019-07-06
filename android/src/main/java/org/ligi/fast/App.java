package org.ligi.fast;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.settings.AndroidFASTSettings;
import org.ligi.fast.settings.FASTSettings;
import org.ligi.tracedroid.TraceDroid;

import java.io.File;
import java.lang.ref.WeakReference;

public class App extends Application {

    private static FASTSettings settings;
    private static App appInstance;

    public static final String LOG_TAG = "FAST App Search";

    public interface PackageChangedListener {
        public void onPackageChange(AppInfoList appInfoList);
    }

    public static PackageChangedListener packageChangedListener;
    public static WeakReference<AppInfoList> backingAppInfoList;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        TraceDroid.init(this);
        settings = new AndroidFASTSettings(App.this);

        Log.d(LOG_TAG, "onCreate");
    }

    public static FASTSettings getSettings() {
        return settings;
    }


    private static int getThemeByString(String theme) {

        switch (theme) {
            case "transparent":
                return R.style.transparent_dark;

            case "transparent_light":
                return R.style.transparent_light;

            case "dark":
                return R.style.dark;

            case "light":
            default:
                return R.style.light;

        }
    }

    public static void injectSettingsForTesting(FASTSettings newSettings) {
        settings = newSettings;
    }

    public static void applyTheme(Activity activity) {
        applyTheme(activity, getSettings().getTheme());
    }

    public static void applyTheme(Activity activity, final String theme) {
        activity.setTheme(getThemeByString(theme));
    }

    public static String getStoreURL4PackageName(String pname) {
        return TargetStore.STORE_URL + pname;
    }

    public static File getBaseDir() {
        return appInstance.getFilesDir();
    }
}
