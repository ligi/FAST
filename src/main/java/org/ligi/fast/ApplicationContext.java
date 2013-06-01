package org.ligi.fast;

import android.app.Activity;
import android.app.Application;

import org.ligi.tracedroid.TraceDroid;

public class ApplicationContext extends Application {

    public final static String getStoreURL4PackageName(String pname) {
        return TargetStore.STORE_URL + pname;
    }

    private FASTPrefs mPrefs;

    public FASTPrefs getPrefs() {
        if (mPrefs == null)
            mPrefs = new FASTPrefs(this);
        return mPrefs;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        TraceDroid.init(this);
    }

    public void applyTheme(Activity activity) {

        if (getPrefs().getTheme().equals("light"))
            activity.setTheme(R.style.light);
        else if (getPrefs().getTheme().equals("dark"))
            activity.setTheme(R.style.dark);
            // and transparent dark/light
        else if (getPrefs().getTheme().equals("transparent"))
            activity.setTheme(R.style.transparent_dark);
        else if (getPrefs().getTheme().equals("transparent_light"))
            activity.setTheme(R.style.transparent_light);

    }

}
