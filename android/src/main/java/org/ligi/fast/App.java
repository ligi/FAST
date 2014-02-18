package org.ligi.fast;

import android.app.Activity;
import android.app.Application;

import org.ligi.fast.model.AppInfo;
import org.ligi.fast.settings.AndroidFASTSettings;
import org.ligi.fast.settings.FASTSettings;
import org.ligi.tracedroid.TraceDroid;

import java.io.File;
import java.util.List;

public class App extends Application {

    private static FASTSettings settings;
    private static App appInstance;

    public static final String LOG_TAG = "FAST App Search";

    public interface PackageChangedListener {
        public void onPackageChange(List<AppInfo> appInfoList);
    }

    public static PackageChangedListener packageChangedListener;

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        TraceDroid.init(this);
        settings = new AndroidFASTSettings(App.this);
    }

    public static FASTSettings getSettings() {
        return settings;
    }

    public static void applyTheme(Activity activity) {

        if (getSettings().getTheme().equals("light")) {
            activity.setTheme(R.style.light);
        } else if (getSettings().getTheme().equals("dark")) {
            activity.setTheme(R.style.dark);
            // and transparent dark/light
        } else if (getSettings().getTheme().equals("transparent")) {
            activity.setTheme(R.style.transparent_dark);
        } else if (getSettings().getTheme().equals("transparent_light")) {
            activity.setTheme(R.style.transparent_light);
        }
    }

    public final static String getStoreURL4PackageName(String pname) {
        return TargetStore.STORE_URL + pname;
    }

    public  static File getBaseDir() {
        return appInstance.getFilesDir();
    }
}
