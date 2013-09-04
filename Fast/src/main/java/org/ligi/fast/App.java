package org.ligi.fast;

import android.app.Activity;
import android.app.Application;

import org.ligi.tracedroid.TraceDroid;

public class App extends Application {

    private static FASTSettings settings;

    @Override
    public void onCreate() {
        super.onCreate();
        TraceDroid.init(this);
        settings = new FASTSettings(App.this);
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

}
