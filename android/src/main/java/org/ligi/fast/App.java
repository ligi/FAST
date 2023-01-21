package org.ligi.fast;

import android.app.Activity;
import android.app.Application;
import android.support.annotation.Nullable;

import org.ligi.fast.model.AppInfo;
import org.ligi.fast.model.AppInfoList;
import org.ligi.fast.settings.AndroidFASTSettings;
import org.ligi.fast.settings.FASTSettings;
import org.ligi.fast.util.AppInfoListStore;
import org.ligi.tracedroid.TraceDroid;

import java.io.File;
import java.util.List;

public class App extends Application {

    private static FASTSettings settings;
    private static App appInstance;

    public static final String LOG_TAG = "FAST App Search";

    public interface PackageChangedListener {
        /**
         * Update information held about one or more packages.
         * The app info list is expected to be complete for every changed package. Any existing
         * records will only be kept if they still apply to the new list.
         *
         * @param packageNames The names of all packages affected in this change
         * @param appInfoList  The full list or activities to track for those packages
         */
        void onPackageChange(@Nullable List<String> packageNames, @Nullable List<AppInfo> appInfoList);
    }

    public static class OfflinePackageChangedListener implements PackageChangedListener {
        @Override
        public void onPackageChange(@Nullable List<String> packageNames, @Nullable List<AppInfo> appInfoList) {
            AppInfoListStore store = new AppInfoListStore(appInstance);
            AppInfoList savedList = store.load();
            savedList.onPackageChange(packageNames, appInfoList);
            store.save(savedList);
        }
    }

    private static PackageChangedListener packageChangedListener;

    /*TODO
     * The {@link OfflinePackageChangedListener} is currently an unused fallback.
     * FAST no longer registers implicit broadcasts to update the app list while it isn't running
     * because this is discouraged by google and on api 26+ only allowed for exempted broadcasts.
     * The SearchActivity lifecycle currently ensures the following things:
     *  - The Activity unconditionally fetches a complete, up-to-date app list onCreate()
     *  - There is always a PackageChangedListener registered between onCreate() and onDestroy()
     *  - User-generated data is saved in onPause()
     *  As a result, it is redundant to update the app list while FAST isn't running.
     * However, it could be desirable to update the icon cache while FAST isn't running.
     * Again, this would only work below api 26. And while an argument could be made that older
     * apis tend to run on older and weaker devices that need an up-to-date icon cache right on
     * startup, but the argument also goes the other way in that taking up system resources in the
     * background is even more an issue if it impacts other running apps.
     */
    public static PackageChangedListener getPackageChangedListener() {
        return packageChangedListener == null ? new OfflinePackageChangedListener() : packageChangedListener;
    }

    public static void registerPackageChangedListener(PackageChangedListener packageChangedListener) {
        App.packageChangedListener = packageChangedListener;
    }

    public static void unregisterPackageChangedListener(PackageChangedListener packageChangedListener) {
        App.packageChangedListener = null;
    }

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
